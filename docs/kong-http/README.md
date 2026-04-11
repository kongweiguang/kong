# kong-http v2 迁移说明

## 核心变化

- 旧入口 `ReqBuilder/*OK` 已移除。
- 新入口：`Req` + `HttpRequestSpec` + `KongHttpClient`。
- 请求构建改为 Pipeline：
  `MethodBodyStep -> ContentTypeStep -> CookieStep -> TagStep -> BuildRequestStep`。
- Body 构建改为策略分发：`RawBodyEncoder`、`FormUrlEncodedEncoder`、`MultipartEncoder`。
- `Client.of(conf)` 改为 `ConfApplier` 注册链。

## 典型写法

```java
HttpRequestSpec spec = Req.post("http://localhost:8080/post")
        .contentType(ContentType.JSON.v())
        .body("{\"name\":\"kong\"}")
        .build();

Res res = KongHttpClient.executeBlocking(spec);
```

## 失败语义

- 失败总是可观测：会抛异常，并且若配置了 `fail(...)` 也会回调。
- 不再通过 `null` 返回值表达失败。

## 监听器上下文变化

- `SSEListener` / `WSListener` 的回调参数从旧 Builder 改为 `HttpRequestSpec`。
- 上下文通过 `Request.tag(HttpRequestSpec.class)` 传递。
