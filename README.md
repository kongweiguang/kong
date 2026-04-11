<h1 align="center" style="text-align:center;">
  kong
</h1>
<p align="center">
  <strong>Java 工具库</strong>
</p>

<p align="center">
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
        <img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
    </a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
        <img src="https://img.shields.io/badge/JDK-21-green.svg" alt="jdk-21" />
    </a>
    <br />
</p>

<br/>

<hr />

# 工具包介绍

- `kong-bom`: 版本管理
- `kong-all`: 聚合所有工具包
- `kong-core`: 通用基础工具
- `kong-http`: 基于 OkHttp 的 HTTP 客户端封装
- `kong-json`: 基于 Jackson 的 JSON 工具
- `kong-socket`: 基于 Java NIO 的网络工具
- `kong-ai`: 大模型工具封装

其他模块的详细说明建议查看各自模块下的 `README.md`。

# http

`kong-http` 是一个基于 OkHttp 的轻量封装，统一了请求构建、同步/异步调用、重试、SSE、WebSocket 和响应解析。

如果后续需要让 AI 继续修改 `kong-http` 代码，先看 [docs/kong-http/README.md](docs/kong-http/README.md)。

## 快速上手

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

最简单的请求:

```java
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;

Res res = Req.get("https://httpbin.org/get").ok();

System.out.println(res.code());
System.out.println(res.str());
```

## 常用栗子

### 创建请求

```java
Req.get("https://example.com");
Req.post("https://example.com");
Req.put("https://example.com");
Req.delete("https://example.com");
Req.formUrlencoded("https://example.com/form");
Req.multipart("https://example.com/upload");
Req.ws("ws://localhost:8080/ws");
Req.sse("http://localhost:8080/sse");
```

手动组装 URL:

```java
Res res = Req.of()
        .scheme("https")
        .host("api.example.com")
        .path("users")
        .path("detail")
        .query("id", 1)
        .ok();
```

### Query、Header、Cookie

```java
Res res = Req.get("https://httpbin.org/get")
        .query("page", 1)
        .query("tag", java.util.List.of("java", "http"))
        .header("X-Trace-Id", "trace-001")
        .bearer("token-value")
        .cookie("sid", "abc123")
        .ok();
```

如果参数已经编码好，可以使用 `encodedQuery(...)`。

### JSON 请求体

```java
User user = new User();
user.setName("kong");
user.setAge(18);

Res res = Req.post("https://httpbin.org/post")
        .json(user)
        .ok();
```

发送原始文本:

```java
Res res = Req.post("https://httpbin.org/post")
        .body("plain text body", ContentType.TEXT_PLAIN.v())
        .ok();
```

### 表单和文件上传

`application/x-www-form-urlencoded`:

```java
Res res = Req.formUrlencoded("https://example.com/form")
        .form("name", "kong")
        .form("age", 18)
        .ok();
```

`multipart/form-data`:

```java
Res res = Req.multipart("https://example.com/upload")
        .form("bizType", "avatar")
        .file("file", "avatar.png", "C:/temp/avatar.png")
        .ok();
```

### 异步请求

`okAsync()` 返回 `CompletableFuture`。如果设置了 `success(...)` 或 `fail(...)` 回调，异步失败会走回调；如果不设置回调，异常会在 `join()` / `get()` 时抛出。

```java
CompletableFuture<Res> future = Req.get("https://httpbin.org/get")
        .success(r -> System.out.println(r.code()))
        .fail(Throwable::printStackTrace)
        .okAsync();

Res res = future.join();
```

### 响应解析

```java
Res res = Req.get("https://httpbin.org/json").ok();

String body = res.str();
byte[] bytes = res.bytes();
JsonNode node = res.node();
Map<String, Object> map = res.map(new TypeReference<Map<String, Object>>() {});
int code = res.code();
boolean ok = res.isOk();
String contentType = res.contentType();
```

下载文件:

```java
Res res = Req.get("https://example.com/file.txt").ok();
res.file("C:/temp/file.txt");
```

### 超时、日志、代理、重试

单次请求配置:

```java
Res res = Req.get("https://example.com")
        .timeout(Duration.ofSeconds(3))
        .log(ReqLog.console, HttpLoggingInterceptor.Level.BODY)
        .retry(r -> r.maxAttempts(3).delay(Duration.ofSeconds(1)))
        .config(c -> c.followRedirects(false))
        .ok();
```

全局配置:

```java
OK.conf()
        .proxy("127.0.0.1", 7890)
        .followRedirects(true)
        .followSslRedirects(true);
```

说明:

- `Req` 每次创建 builder 时都会复制一份全局 `Conf`
- 单次 `.config(...)` 只影响当前请求，不会污染其他请求
- 默认开启 SSL、HTTP 重定向、HTTPS 重定向
- `GET` 和 `HEAD` 不会真正携带请求体
- `POST`、`PUT`、`PATCH` 在没有显式 body 时会自动补空请求体

### WebSocket

```java
WSListener listener = new WSListener() {
    @Override
    public void open(WSReqBuilder req, Res res) {
        send("hello");
    }

    @Override
    public void msg(WSReqBuilder req, String text) {
        System.out.println(text);
    }
};

WebSocket ws = Req.ws("ws://localhost:8080/ws")
        .header("X-App", "demo")
        .wsListener(listener)
        .ok();
```

### SSE

```java
SSEListener listener = new SSEListener() {
    @Override
    public void event(SSEReqBuilder req, SseEvent msg) {
        System.out.println(msg.data());
        if ("done".equals(msg.data())) {
            closeCon();
        }
    }
};

EventSource source = Req.sse("http://localhost:8080/sse")
        .sseListener(listener)
        .ok();
```
