# kong-http 迁移说明

## 关键变化

- 移除旧的 `ReqBuilder/*OK` 执行链路
- 统一入口为 `Req` + `HttpRequestSpec` + `KongHttpClient`
- 请求构建改为固定步骤 Pipeline：
  `MethodBodyStep -> ContentTypeStep -> CookieStep -> TagStep -> BuildRequestStep`
- Body 编码改为策略分发：`RawBodyEncoder`、`FormUrlEncodedEncoder`、`MultipartEncoder`
- `Client.of(conf)` 改为 `ConfApplier` 注册链，配置职责更清晰

## 新版写法

```java
HttpRequestSpec spec = Req.post("http://localhost:8080/post")
        .contentType(ContentType.JSON.v())
        .body("{\"name\":\"kong\"}")
        .build();

Res res = KongHttpClient.executeBlocking(spec);
```

## 失败语义

- 失败必须可观测：会抛出异常，同时触发 `fail(...)` 回调
- 不再通过 `null` 返回值表达失败

## SSE / WS 差异

- `SSEListener` / `WSListener` 通过 Builder 绑定到 `HttpRequestSpec`
- 运行时可通过 `Request.tag(HttpRequestSpec.class)` 取回原始请求规格