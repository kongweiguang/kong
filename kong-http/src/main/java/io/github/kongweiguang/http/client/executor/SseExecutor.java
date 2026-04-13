package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.spec.SseReqSpec;
import io.github.kongweiguang.http.client.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.retry.RequestRetryExecutor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

/**
 * SSE 执行器。
 *
 * @author kongweiguang
 */
public final class SseExecutor extends AbstractExecutor<SseReqSpec, EventSource> {

    private static final RequestPipeline<SseReqSpec> PIPELINE = RequestPipeline.sse();

    /**
     * 创建 SSEExecutor instance
     */
    public SseExecutor(SseReqSpec spec, OkHttpClient client, RequestRetryExecutor<EventSource> retryExecutor) {
        super(spec, client, retryExecutor);
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
