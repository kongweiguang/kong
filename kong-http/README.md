# kong-http

`kong-http` uses an immutable request spec and a unified client entry.

## Quick Start

```java
HttpRequestSpec spec = Req.post("https://httpbin.org/post")
        .contentType(ContentType.JSON.v())
        .body("{\"hello\":\"world\"}")
        .build();

Res res = KongHttpClient.executeBlocking(spec);
System.out.println(res.code());
```

## Design

- `HttpRequestSpec`: immutable request model.
- `KongHttpClient`: unified HTTP/SSE/WS facade.
- `RequestPipeline`: deterministic build steps.
- `BodyEncoderFactory`: strategy dispatch for raw/form/multipart.
- `Client.of(conf)`: `ConfApplier` chain for config-to-OkHttp mapping.

## Async Callback

```java
HttpRequestSpec spec = Req.get("https://httpbin.org/get")
        .success(res -> System.out.println(res.code()))
        .fail(Throwable::printStackTrace)
        .build();

KongHttpClient.execute(spec);
```

## SSE / WS

```java
HttpRequestSpec sseSpec = Req.sse("https://example.com/sse")
        .sseListener(new SSEListener() {
            @Override
            public void event(HttpRequestSpec req, SseEvent msg) {
                System.out.println(msg.data());
            }
        })
        .build();

KongHttpClient.executeSse(sseSpec);
```
