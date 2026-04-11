package io.github.kongweiguang.http.client.v2.executor;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.v2.ResultHandler;
import io.github.kongweiguang.http.client.v2.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.v2.retry.RetryPolicy;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * WebSocket 执行器。
 */
public final class WSExecutor extends AbstractExecutor<WebSocket> {
    private static final RequestPipeline PIPELINE = new RequestPipeline();

    public WSExecutor(HttpRequestSpec spec, OkHttpClient client, RetryPolicy<WebSocket> retryPolicy, ResultHandler<WebSocket> handler) {
        super(spec, client, retryPolicy, handler);
    }

    @Override
    protected WebSocket executeCore() {
        Request request = PIPELINE.build(spec());
        return client().newWebSocket(request, spec().wsListener());
    }
}


