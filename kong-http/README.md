# kong-http

`kong-http` 是一个基于 OkHttp 的轻量 HTTP 客户端封装，使用链式 API 完成请求构建，并通过 `ok()` / `okAsync()` 发起调用。

这份 README 以快速入门为主，示例内容来自模块里的测试用例，目标是让使用者先跑通常见 HTTP 请求。

## 安装

Maven:

```xml
<dependency>
    <groupId>io.github.kongweiguang</groupId>
    <artifactId>kong-http</artifactId>
    <version>0.6</version>
</dependency>
```

Gradle:

```groovy
implementation("io.github.kongweiguang:kong-http:0.6")
```

## 核心用法

`Req` 是请求入口，用来创建请求。

```java
Req.get("https://example.com");
Req.post("https://example.com");
Req.put("https://example.com");
Req.delete("https://example.com");
Req.formUrlencoded("https://example.com/form");
Req.multipart("https://example.com/upload");
Req.sse("https://example.com/sse");
Req.ws("ws://example.com/ws");
```

`Res` 是普通 HTTP 请求的响应对象，用来读取状态码、响应头和响应体。

## 第一个请求

```java
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;

Res res = Req.get("http://localhost:8080/get_string")
        .query("a", "1")
        .query("b", "2")
        .ok();

System.out.println(res.code());
System.out.println(res.str());
```

## 常见请求

### GET + Query 参数

```java
Res res = Req.get("http://localhost:8080/get_string")
        .query("a", "1")
        .query("b", "2")
        .query("d", java.util.Arrays.asList("0", "9", "8"))
        .ok();
```

### POST + JSON

```java
User user = new User()
        .setName("kong")
        .setAge(18);

Res res = Req.post("http://localhost:8080/post_json")
        .query("source", "demo")
        .json(user)
        .ok();
```

如果已经有原始文本，也可以直接指定请求体和内容类型:

```java
import io.github.kongweiguang.http.client.consts.ContentType;

Res res = Req.post("http://localhost:8080/post_body")
        .body("plain text body", ContentType.TEXT_PLAIN.value())
        .ok();
```

### 表单提交

`application/x-www-form-urlencoded`:

```java
Res res = Req.formUrlencoded("http://localhost:8080/post_form")
        .form("a", "1")
        .form("b", "2")
        .ok();
```

`multipart/form-data`:

```java
Res res = Req.multipart("http://localhost:8080/post_mul_form")
        .form("bizType", "avatar")
        .file("file", "avatar.png", java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get("C:", "temp", "avatar.png")))
        .ok();
```

## Header 和 Cookie

```java
import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.UserAgent;

Res res = Req.get("http://localhost:8080/header")
        .contentType(ContentType.JSON.value())
        .userAgent(UserAgent.MacOS.CHROME.value())
        .bearer("token-value")
        .header("X-Trace-Id", "trace-001")
        .cookie("sid", "abc123")
        .ok();
```

如果你已经有一批 header 或 cookie，也可以使用 `headers(Map)` 和 `cookies(Map)` 批量设置。

## 读取响应

```java
import com.fasterxml.jackson.core.type.TypeReference;

Res res = Req.get("http://localhost:8080/get_json").ok();

int code = res.code();
boolean ok = res.isOk();
String text = res.str();
byte[] bytes = res.bytes();
User obj = res.obj(User.class);
java.util.List<User> list = res.obj(new TypeReference<java.util.List<User>>() {});
String traceId = res.header("X-Trace-Id");
java.util.Map<String, java.util.List<String>> headers = res.headers();
```

常用读取方法:

- `res.str()` 读取字符串
- `res.bytes()` 读取字节数组
- `res.obj(...)` / `res.list(...)` / `res.map(...)` 解析 JSON
- `res.stream()` 获取输入流
- `res.code()` / `res.isOk()` 判断请求结果

注意: `ResponseBody` 只能消费一次，通常在 `str()`、`bytes()`、`obj()` 这类方法里选择一种读取方式即可。

## 异步请求

`ok()` 同步执行时会直接返回结果或直接抛出异常，不会触发 `success(...)` / `fail(...)` 回调。

`okAsync()` 返回 `CompletableFuture<Res>`，只有异步执行时才会触发 `success(...)` / `fail(...)` 回调。

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

CompletableFuture<Res> future = Req.get("http://localhost:8080/get")
        .query("a", "1")
        .success(r -> System.out.println(r.str()))
        .fail(t -> System.out.println("error: " + t.getMessage()))
        .okAsync();

Res res = future.get(3, TimeUnit.MINUTES);
```

## 全局配置和单次配置

全局配置通过 `Conf.global()` 设置，适合代理、线程池、连接池、拦截器这类公共能力。

```java
import io.github.kongweiguang.http.client.core.Conf;
import okhttp3.ConnectionPool;

import java.net.Proxy;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

Conf.global()
        .proxy("127.0.0.1", 7890)
        .proxy(Proxy.Type.SOCKS, "127.0.0.1", 7890)
        .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
        .exec(Executors.newCachedThreadPool());
```

如果只想影响当前请求，可以直接挂在请求上:

```java
import java.time.Duration;

Res res = Req.get("http://localhost:8080/timeout")
        .timeout(Duration.ofSeconds(1))
        .ok();
```

## 快速排查问题

- 请求没发出去时，优先检查 URL、代理和超时配置。
- 返回 JSON 时，直接用 `json(obj)` 发送对象会更省事，底层会使用 Jackson 序列化。
- 上传文件时，优先使用 `Req.multipart(...)`，普通表单使用 `Req.formUrlencoded(...)`。
- 需要看请求细节时，可以配合日志功能一起使用:

```java
import io.github.kongweiguang.http.client.core.ReqLog;
import okhttp3.logging.HttpLoggingInterceptor;

Req.get("http://localhost:8080/get")
        .log(ReqLog.console, HttpLoggingInterceptor.Level.BODY)
        .ok();
```

## 更多能力

`kong-http` 还支持这些能力，只是这里不展开:

- 重试: `retry(...)`
- SSE: `Req.sse(...).sseListener(...).ok()`
- WebSocket: `Req.ws(...).wsListener(...).ok()`

如果你想看更完整的使用方式，可以直接参考这些测试类:

- `kong-http-test/src/test/java/io/github/kongweiguang/http/client/core/RestTest.java`
- `kong-http-test/src/test/java/io/github/kongweiguang/http/client/core/BodyTest.java`
- `kong-http-test/src/test/java/io/github/kongweiguang/http/client/core/FormTest.java`
- `kong-http-test/src/test/java/io/github/kongweiguang/http/client/core/AsyncTest.java`
- `kong-http-test/src/test/java/io/github/kongweiguang/http/client/core/ConfigTest.java`
- `kong-http-test/src/test/java/io/github/kongweiguang/http/client/core/RetryTest.java`
- `kong-http-test/src/test/java/io/github/kongweiguang/http/client/sse/SseTest.java`
- `kong-http-test/src/test/java/io/github/kongweiguang/http/client/ws/WsTest.java`
