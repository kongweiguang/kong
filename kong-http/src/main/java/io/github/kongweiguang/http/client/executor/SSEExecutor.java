package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.ResultHandler;
import io.github.kongweiguang.http.client.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.retry.RetryPolicy;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

/**
 * SSE 执行器。
 *
 * @author kongweiguang
 */
public final class SSEExecutor extends AbstractExecutor<EventSource> {

    private static final RequestPipeline PIPELINE = new RequestPipeline();

    /**
     * 创建 SSEExecutor instance
     */
    public SSEExecutor(HttpRequestSpec spec, OkHttpClient client, RetryPolicy<EventSource> retryPolicy, ResultHandler<EventSource> handler) {
        super(spec, client, retryPolicy, handler);
    }

    /**
     * 执行 SSE 建连逻辑。
     */
    @Override
    protected EventSource execute0() {
        Request request = PIPELINE.build(spec());
        return EventSources.createFactory(client()).newEventSource(request, spec().sseListener());
    }
}

