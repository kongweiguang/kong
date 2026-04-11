package io.github.kongweiguang.http.client.v2.executor;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.v2.ResultHandler;
import io.github.kongweiguang.http.client.v2.retry.RetryPolicy;
import io.github.kongweiguang.http.client.v2.pipeline.RequestPipeline;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

/**
 * SSE 执行器。
 */
public final class SSEExecutor extends AbstractExecutor<EventSource> {
    private static final RequestPipeline PIPELINE = new RequestPipeline();

    public SSEExecutor(HttpRequestSpec spec, OkHttpClient client, RetryPolicy<EventSource> retryPolicy, ResultHandler<EventSource> handler) {
        super(spec, client, retryPolicy, handler);
    }

    @Override
    protected EventSource executeCore() {
        Request request = PIPELINE.build(spec());
        return EventSources.createFactory(client()).newEventSource(request, spec().sseListener());
    }
}


