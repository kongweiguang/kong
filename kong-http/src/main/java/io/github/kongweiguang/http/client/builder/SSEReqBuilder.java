package io.github.kongweiguang.http.client.builder;

import io.github.kongweiguang.http.client.core.ReqType;
import io.github.kongweiguang.http.client.ok.SSEOK;
import io.github.kongweiguang.http.client.sse.SSEListener;
import okhttp3.OkHttpClient;
import okhttp3.sse.EventSource;

import java.util.concurrent.CompletableFuture;

import static io.github.kongweiguang.core.lang.Assert.notNull;

/**
 * Server-Sent Events请求构建器
 *
 * @author kongweiguang
 */
public class SSEReqBuilder extends HttpReqBuilder<SSEReqBuilder, EventSource> {
    private SSEListener sseListener;

    public SSEReqBuilder() {
        super();
        reqType(ReqType.sse);
    }

    @Override
    protected CompletableFuture<EventSource> execute(OkHttpClient client) {
        return SSEOK.ok(this, client);
    }

    /**
     * sse协议调用时的监听函数
     *
     * @param sseListener 监听函数
     * @return ReqBuilder {@link ReqBuilder}
     */
    public SSEReqBuilder sseListener(SSEListener sseListener) {
        notNull(sseListener, "sseListener must not be null");

        this.sseListener = sseListener;
        return this;
    }

    public SSEListener sseListener() {
        return sseListener;
    }
}