<h1 align="center">kong</h1>

<p align="center">
  <strong>一组轻量、顺手、面向日常开发的 Java 工具库</strong>
</p>

<p align="center">
  <a href="https://search.maven.org/search?q=g:io.github.kongweiguang">
    <img src="https://img.shields.io/maven-central/v/io.github.kongweiguang/kong.svg?label=Maven%20Central" alt="Maven Central" />
  </a>
  <a href="https://www.oracle.com/java/technologies/downloads/">
    <img src="https://img.shields.io/badge/JDK-21-green.svg" alt="JDK 21" />
  </a>
  <a href="https://www.apache.org/licenses/LICENSE-2.0.txt">
    <img src="https://img.shields.io/badge/license-Apache--2.0-blue.svg" alt="Apache 2.0" />
  </a>
</p>

---

`kong` 是一个 Java 多模块工具库，目标是把项目里常见但容易写散的能力收拢成简洁 API：HTTP 请求、JSON 处理、重试、事件总线、TCP Socket 等。

如果你只是想快速发 HTTP 请求，可以先从 `kong-http` 开始；如果你想要基础工具、JSON 构建或 TCP 通信，也可以按模块单独引入。

## 为什么用它

- 链式 API，代码读起来接近真实调用过程。
- 模块独立，按需引入，不需要整包绑进项目。
- 基于成熟生态封装：`kong-http` 基于 OkHttp，`kong-json` 基于 Jackson。
- 覆盖常见开发场景：同步/异步 HTTP、JSON 序列化、响应解析、重试、SSE、WebSocket、事件分发、TCP Server/Client。

## 模块一览

| 模块 | 适合场景 | 文档 |
| --- | --- | --- |
| `kong-http` | HTTP 客户端、异步请求、重试、SSE、WebSocket | [kong-http/README.md](kong-http/README.md) |
| `kong-json` | JSON 构建、对象转换、Jackson 快捷封装 | [kong-json/README.md](kong-json/README.md) |
| `kong-core` | 断言、字符串、对象、重试、事件总线、管道/责任链等基础能力 | [kong-core/README.md](kong-core/README.md) |
| `kong-socket` | 支持 NIO/AIO 可选驱动的 TCP Server/Client 工具 | [kong-socket/README.md](kong-socket/README.md) |
| `kong-http-test` | HTTP 示例和集成测试工程 | [kong-http-test](kong-http-test) |

## 环境要求

- JDK 21+
- Maven 3.8+

## 安装

按需选择模块即可。

### Maven

```xml
<dependency>
    <groupId>io.github.kongweiguang</groupId>
    <artifactId>kong-http</artifactId>
    <version>0.6</version>
</dependency>
```

### Gradle

```groovy
implementation "io.github.kongweiguang:kong-http:0.6"
```

### Gradle Kotlin DSL

```kotlin
implementation("io.github.kongweiguang:kong-http:0.6")
```

其他模块只需要替换 `artifactId`：

```text
kong-core
kong-json
kong-socket
```

如果 Maven Central 还没有同步到目标版本，也可以先本地安装：

```bash
mvn clean install
```

## 30 秒上手：发送 HTTP 请求

```java
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;

public class Demo {
    public static void main(String[] args) {
        Res res = Req.get("https://httpbin.org/get")
                .query("name", "kong")
                .header("X-Trace-Id", "demo-001")
                .ok();

        System.out.println(res.code());
        System.out.println(res.str());
    }
}
```

## 常用示例

### POST JSON

```java
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;

import java.util.Map;

Res res = Req.post("https://httpbin.org/post")
        .json(Map.of("name", "kong", "age", 18))
        .ok();

System.out.println(res.str());
```

### 异步请求

```java
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;

import java.util.concurrent.CompletableFuture;

CompletableFuture<Res> future = Req.get("https://httpbin.org/get")
        .success(res -> System.out.println("status: " + res.code()))
        .fail(Throwable::printStackTrace)
        .okAsync();

Res res = future.join();
```

### 响应解析

```java
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;

import java.util.Map;

Res res = Req.get("https://httpbin.org/json").ok();

String text = res.str();
int code = res.code();
boolean success = res.isOk();
```

如果响应体是 JSON，可以按对象、集合或 `JsonNode` 读取：

```java
Res res = Req.get("https://example.com/user").ok();

User user = res.obj(User.class);
Map<String, Object> map = res.map(new TypeReference<Map<String, Object>>() {});
```

注意：底层 `ResponseBody` 通常只能消费一次，`str()`、`bytes()`、`obj()`、`map()` 这类读取方式建议按需选择一种。

### JSON 构建

```java
import io.github.kongweiguang.json.Json;

String json = Json.object()
        .put("name", "kong")
        .put("age", 18)
        .put("active", true)
        .array("tags", array -> array.add("java").add("tools"))
        .toPrettyJson();

System.out.println(json);
```

### 通用重试

```java
import io.github.kongweiguang.core.retry.RetryExecutor;
import io.github.kongweiguang.core.retry.RetryPolicy;

import java.time.Duration;

String value = RetryExecutor.execute(
        RetryPolicy.<String>builder()
                .maxAttempts(3)
                .interval(Duration.ofMillis(200))
                .retryIf(ctx -> ctx.error() != null)
                .build(),
        () -> callRemoteService()
);
```

### TCP Echo Server

```java
import io.github.kongweiguang.socket.tcp.KongTcpServer;
import io.github.kongweiguang.socket.tcp.TcpDriver;
import io.github.kongweiguang.socket.tcp.codec.Codecs;

KongTcpServer server = KongTcpServer.builder()
        .bind("0.0.0.0", 8888)
        .driver(TcpDriver.AIO)
        .workerThreads(4)
        .codec(Codecs.newline())
        .handler((connection, message) -> connection.send(message))
        .build()
        .start();
```

## 从源码运行

```bash
git clone https://github.com/kongweiguang/kong.git
cd kong
mvn clean test
```

只测试某个模块：

```bash
mvn -pl kong-http test
```

## 更多文档

- HTTP 客户端完整示例：[kong-http/README.md](kong-http/README.md)
- JSON 工具完整示例：[kong-json/README.md](kong-json/README.md)
- Socket 工具完整示例：[kong-socket/README.md](kong-socket/README.md)
- Core 事件总线示例：[kong-core/README.md](kong-core/README.md)

## License

`kong` 使用 [Apache License 2.0](LICENSE) 开源协议。
