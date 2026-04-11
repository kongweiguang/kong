package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.ResultHandler;
import io.github.kongweiguang.http.client.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.retry.RetryPolicy;
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


