package io.github.kongweiguang.http.client.ok;

import io.github.kongweiguang.http.client.OK;
import io.github.kongweiguang.http.client.builder.SSEReqBuilder;
import okhttp3.OkHttpClient;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * SSE请求发送器
 *
 * @author kongweiguang
 */
public class SSEOK extends OK<SSEReqBuilder, EventSource> {

    private SSEOK(SSEReqBuilder reqBuilder, OkHttpClient client) {
        super(reqBuilder, client);
    }

    /**
     * <h2>发送SSE请求</h2>
     *
     * @param reqBuilder SSE请求参数 {@link SSEReqBuilder}
     * @param client     OkHttpClient {@link OkHttpClient}
     * @return EventSource {@link EventSource}
     */
    public static CompletableFuture<EventSource> ok(SSEReqBuilder reqBuilder, OkHttpClient client) {
        return new SSEOK(reqBuilder, client).ojbk();
    }

    private CompletableFuture<EventSource> ojbk() {
        return CompletableFuture.supplyAsync(this::execute, reqBuilder().config().exec());
    }

    /**
     * 执行SSE请求
     *
     * @return EventSource对象
     */
    @Override
    protected EventSource execute() {
        return EventSources.createFactory(client()).newEventSource(request(), reqBuilder().sseListener());
    }

}