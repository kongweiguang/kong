package io.github.kongweiguang.http.client.spec;

import io.github.kongweiguang.http.client.executor.SseExecutor;
import io.github.kongweiguang.http.client.retry.NoRetryExecutor;
import io.github.kongweiguang.http.client.sse.SseListener;
import okhttp3.OkHttpClient;
import okhttp3.sse.EventSource;

import static io.github.kongweiguang.core.lang.Assert.notNull;

/**
 * Server-Sent Events请求构建器
 *
 * @author kongweiguang
 */
public class SseReqSpec extends ReqSpec<SseReqSpec, EventSource> {
    private SseListener sseListener;

    public SseReqSpec() {
        super();
    }

    @Override
    protected EventSource execute(OkHttpClient client) {
        return new SseExecutor(this, client, new NoRetryExecutor<>()).executeBlocking();
    }


    /**
     * sse协议调用时的监听函数
     *
     * @param sseListener 监听函数
     * @return ReqBuilder {@link ReqSpec}
     */
    public SseReqSpec sseListener(SseListener sseListener) {
        notNull(sseListener, "sseListener must not be null");

        this.sseListener = sseListener;
        return this;
    }

    /**
     * 获取sse协议调用时的监听函数
     *
     * @return
     */
    public SseListener sseListener() {
        return sseListener;
    }
}
