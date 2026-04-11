# kong-http 代码阅读笔记

本文档基于 `kong-http` 模块源码阅读整理，目标不是教使用，而是帮助后续维护者或 AI 在修改代码前快速建立上下文。

## 1. 模块定位

`kong-http` 是对 OkHttp 4.12.0 的二次封装，提供三类能力：

- HTTP 请求构建与执行
- SSE 与 WebSocket 的统一入口
- 响应包装、日志、超时、代理、重试等常见能力

模块依赖：

- `okhttp`
- `okhttp-sse`
- `logging-interceptor`
- `kong-json`

## 2. 目录和核心类

### 2.1 对外入口

- `io.github.kongweiguang.http.client.Req`
  - 整个模块最重要的静态入口
  - 提供 `get/post/put/delete/...`
  - 提供 `formUrlencoded(...)`、`multipart(...)`
  - 提供 `ws(...)`、`sse(...)`
- `io.github.kongweiguang.http.client.Res`
  - OkHttp `Response` 的包装类
  - 负责读取 body、header、cookie、状态码、反序列化、下载文件
- `io.github.kongweiguang.http.client.OK`
  - 三种执行器的抽象基类
  - 暴露 `OK.conf()` 作为全局配置入口

### 2.2 Builder 层

- `ReqBuilder<T, R>`
  - 所有请求构建器的基类
  - 管理 method、url、query、header、cookie、charset、contentType、attachment、单次配置
  - `before()` 是构建过程里的关键钩子
- `HttpReqBuilder<T, R>`
  - 在 `ReqBuilder` 基础上补充 HTTP body、form、multipart、异步回调、重试
- `DefHTTPReqBuilder`
  - 默认 HTTP 请求 builder，没有新增行为，只是具体类型落地
- `WSReqBuilder`
  - WebSocket builder，核心状态是 `WSListener`
- `SSEReqBuilder`
  - SSE builder，继承 `HttpReqBuilder`，核心状态是 `SSEListener`

### 2.3 配置与执行层

- `Conf`
  - 全局/单次请求配置模型
  - 包含代理、连接池、拦截器、超时、SSL、重定向、CookieJar、事件监听器、异步执行器
- `Client`
  - 将 `Conf` 映射成 `OkHttpClient`
  - 通过 `client.newBuilder()` 复制默认客户端，再按 `Conf` 覆盖
- `HttpOK`
  - HTTP 请求实际执行器
  - 负责同步/异步执行、重试策略接入、回调分发
- `SSEOK`
  - 基于 `EventSources.createFactory(client())` 建立 SSE 连接
- `WSOK`
  - 基于 `client().newWebSocket(...)` 建立 WebSocket 连接

### 2.4 协议监听器

- `SSEListener`
  - 继承 `EventSourceListener`
  - 把 OkHttp 回调转换成更贴近模块的 `open/event/fail/closed`
  - 内部保存当前 `EventSource`，可通过 `closeCon()` 主动关闭
- `WSListener`
  - 继承 `WebSocketListener`
  - 暴露 `open/msg/fail/closing/closed`
  - 内部保存当前 `WebSocket`，可通过 `send(...)` / `closeCon()` 操作连接

## 3. 请求生命周期

### 3.1 HTTP 请求

主链路如下：

1. 调用 `Req.get(...)` / `Req.post(...)` 等静态工厂创建 builder
2. `ReqBuilder` 构造函数里执行 `Conf.global().copy()`
3. 用户继续链式设置 URL、Query、Header、Body、Retry、Config
4. 调用 `ok()` 或 `okAsync()`
5. `ReqBuilder.before()` 在发送前统一补齐最终请求
6. `HttpReqBuilder.execute(...)` 委托到 `HttpOK.ok(...)`
7. `HttpOK` 在配置的 `Executor` 上执行
8. `HttpOK.executeWithStrategy()` 套用 `RetryableTask`
9. 成功时返回 `Res`，失败时按同步/异步模式决定抛异常还是调用 `fail(...)`

### 3.2 before() 做了什么

`ReqBuilder.before()` 是最值得先看的方法之一，主要职责：

- 根据 HTTP 方法决定是否允许 body
- 对 `POST` / `PUT` / `PATCH` 在无 body 时自动补空请求体
- 在非 multipart、非 form-url-encoded 时自动拼接 `Content-Type: xxx;charset=yyy`
- 把 method、url、body 一次性写入 OkHttp `Request.Builder`
- 将 cookie map 组装成 `Cookie` 请求头
- 把当前 builder 放进 `Request.tag(...)`

`Request.tag(...)` 非常关键，因为 SSE/WS 监听器后续就是靠它取回原始 builder：

- `eventSource.request().tag(SSEReqBuilder.class)`
- `webSocket.request().tag(WSReqBuilder.class)`

这让监听回调里可以访问原始请求参数和附件。

## 4. URL 与请求体规则

### 4.1 URL 处理

`ReqBuilder.url(...)` 会先调用 `HttpClientUtil.fixUrl(...)` 再交给 `HttpUrl.parse(...)`。

`fixUrl(...)` 的行为特点：

- `null` 或空字符串会回退到 `http://localhost`
- `/path` 会补成 `http://localhost/path`
- `?a=1` / `#frag` 会补成 `http://localhost/?a=1`
- `ws://` 会转换成 `http://`
- `wss://` 会转换成 `https://`

这意味着：

- 对 WebSocket/SSE 而言，协议最终仍走 OkHttp 的 HTTP/HTTPS URL 体系
- 用户如果只传相对路径，模块会自动补全到 localhost

### 4.2 Body 处理

`HttpReqBuilder.addBody()` 分三种分支：

- multipart：
  - 使用 `MultipartBody.Builder`
  - `formMap` 会自动写入 multipart part
- form-url-encoded：
  - 使用 `FormBody.Builder`
- 普通 body：
  - 使用 `RequestBody.create(MediaType.parse(contentType()), body)`

隐含约束：

- `file(...)` 只能在 multipart 下调用，否则直接抛异常
- `form(...)` 只能在 multipart 或 form-url-encoded 下调用，否则直接抛异常
- `json(...)` 实际只是 `body(..., ContentType.JSON.v())` 的快捷方式

## 5. 配置模型

### 5.1 全局配置和单次配置

全局配置通过 `OK.conf()` 获取，本质是 `Conf.global()` 单例。

但是每个 `ReqBuilder` 构造时都会执行 `Conf.global().copy()`，因此：

- 全局配置会作为新请求的默认值
- 单次请求 `.config(...)` 只改当前 builder 的配置副本
- 不会反向污染全局配置
- 也不会影响其他已经创建或未来创建的请求对象

这一点已经被测试覆盖：

- `ConfigBehaviorTest.reqBuilderShouldUseIsolatedConf()`

### 5.2 Conf 字段到 OkHttp 的映射

`Client.of(Conf)` 负责把 `Conf` 应用到 `OkHttpClient.Builder`：

- `httpLoggingInterceptor` -> `addInterceptor`
- `interceptors` -> `addInterceptor`
- `dispatcher` -> `dispatcher(...)`
- `connectionPool` -> `connectionPool(...)`
- `proxy` -> `proxy(...)`
- `proxyAuthenticator` -> `proxyAuthenticator(...)`
- `proxySelector` -> `proxySelector(...)`
- `eventListener` -> `eventListener(...)`
- `cookieJar` -> `cookieJar(...)`
- `followRedirects` / `followSslRedirects` -> 对应开关
- `timeout` -> connect/write/read timeout
- `ssl=false` -> 安装信任所有证书的 SSL 配置

注意点：

- 默认异步执行器是 `Executors.newVirtualThreadPerTaskExecutor()`
- 默认客户端的 dispatcher 上限被设置得非常高
- `ssl(false)` 会关闭证书校验和 hostname 校验，适合测试环境，不适合生产默认开启

## 6. 异步与重试行为

### 6.1 异步

`okAsync()` 最终会在 `Conf.exec()` 指定的线程池中执行。

HTTP 请求的异步结果有两种消费方式：

- 纯 `CompletableFuture` 风格
- `success(...)` / `fail(...)` 回调风格

`HttpOK.executeWithStrategy()` 的行为值得注意：

- 如果设置了 `success` 或 `fail` 任意一个回调：
  - 失败不会直接抛出
  - 会回调 `fail(...)`
  - 返回值可能是 `null`
- 如果没有设置回调：
  - 失败会抛 `KongHttpRuntimeException`

因此后续如果修改异步语义，这里是核心位置。

### 6.2 重试

`HttpReqBuilder` 默认内置一个 `RetryableTask<Res>`：

- 默认 `maxAttempts(1)`，也就是默认不重试
- 默认策略：
  - 有异常时重试
  - 有响应但 `!res.isOk()` 时重试

用户可以通过 `.retry(...)` 覆盖：

- 最大次数
- 延迟
- 自定义 predicate

重试逻辑只应用于普通 HTTP，不作用于 SSE/WS。

## 7. Res 的设计与注意事项

`Res` 是一个薄包装，但使用时有几个很重要的点：

- `str()` 和 `bytes()` 都会消耗响应体
- 多次读取 body 可能失败或得到空结果，这和 OkHttp 原生行为一致
- `obj(...)`、`list(...)`、`map(...)` 本质上也是先 `str()` 再走 `kong-json`
- `file(...)` 直接把响应流复制到目标路径
- `close()` 会关闭底层 `Response`

如果后续要扩展响应缓存、多次读取、trace 信息，`Res` 是最合适的入口。

## 8. 流式协议实现特点

### 8.1 WebSocket

WebSocket 的建立流程：

1. `Req.ws(...)` 创建 `WSReqBuilder`
2. `WSReqBuilder.ok()` -> `WSOK.ok(...)`
3. `WSOK.execute()` -> `client().newWebSocket(request(), wsListener())`
4. 回调时 `WSListener` 从 `request.tag(...)` 取回原始 `WSReqBuilder`

特点：

- `WSListener` 内部持有当前 `WebSocket`
- `send(String)` 实际会转成二进制发送 `ByteString.of(bytes)`
- 文本和二进制消息有两个 `msg(...)` 重载

### 8.2 SSE

SSE 的建立流程：

1. `Req.sse(...)` 创建 `SSEReqBuilder`
2. `SSEReqBuilder.ok()` -> `SSEOK.ok(...)`
3. `SSEOK.execute()` -> `EventSources.createFactory(client()).newEventSource(...)`
4. 回调时 `SSEListener` 从 `request.tag(...)` 取回原始 `SSEReqBuilder`

特点：

- `event(...)` 是唯一必须实现的方法
- `closeCon()` 通过 `EventSource.cancel()` 主动结束连接
- `fail(...)` 中的 `Response` 可能为 `null`，后续修改这里要留意空值安全

## 9. 已有测试覆盖的重点

从测试文件看，当前模块已覆盖这些行为：

- URL 拼装与 query 处理
- header / cookie / body / form / multipart
- 同步与异步请求
- 重试、超时、代理
- 响应解析
- WebSocket / SSE
- 配置隔离、默认值、`proxySelector`、`Content-Type` + `charset` 行为

对后续 AI 改代码最有价值的测试文件：

- `ConfigBehaviorTest`
- `ReqBuilderBehaviorTest`
- `UrlTest`
- `UrlQueryTest`
- `BodyTest`
- `RetryTest`
- `SseTest`
- `WsTest`

## 10. 后续修改建议

如果后续要改 `kong-http`，建议优先判断改动落在哪一层：

- 改请求 API：
  - 先看 `Req`
  - 再看 `ReqBuilder` / `HttpReqBuilder`
- 改配置语义：
  - 先看 `Conf`
  - 再看 `Client`
- 改发送和错误处理：
  - 先看 `HttpOK`
  - 再看相关测试
- 改 SSE / WS：
  - 先看 `SSEReqBuilder` / `WSReqBuilder`
  - 再看 `SSEOK` / `WSOK`
  - 最后看监听器基类
- 改响应解析：
  - 先看 `Res`

高风险点：

- `Res` 的响应体是一次性流，修改解析逻辑容易引入“读两次 body”问题
- `ReqBuilder.before()` 同时影响所有 HTTP 请求，是请求发送前的统一收口
- `Conf.copy()` 决定了全局配置与单次配置是否隔离
- `HttpOK.executeWithStrategy()` 同时处理重试、同步异常和异步回调，改动时最容易引起行为变化

## 11. 建议的 AI 阅读顺序

如果是让 AI 接手改这个模块，可以按下面顺序提供上下文：

1. `Req.java`
2. `ReqBuilder.java`
3. `HttpReqBuilder.java`
4. `Conf.java`
5. `Client.java`
6. `HttpOK.java`
7. 对应测试文件

如果任务涉及流式协议，再补：

8. `WSReqBuilder.java` / `WSOK.java` / `WSListener.java`
9. `SSEReqBuilder.java` / `SSEOK.java` / `SSEListener.java`
