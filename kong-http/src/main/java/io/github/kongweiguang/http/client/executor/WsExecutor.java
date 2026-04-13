package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.retry.RequestRetryExecutor;
import io.github.kongweiguang.http.client.spec.WsReqSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * WebSocket 请求执行器。
 *
 * @author kongweiguang
 */
public final class WsExecutor extends AbstractExecutor<WsReqSpec, WebSocket> {

    /**
     * 定义 pipeline constant
     */
    private static final RequestPipeline<WsReqSpec> PIPELINE = RequestPipeline.ws();

    /**
     * 创建 WSExecutor instance
     */
    public WsExecutor(WsReqSpec spec, OkHttpClient client, RequestRetryExecutor<WebSocket> retryExecutor) {
        super(spec, client, retryExecutor);
    }

    /**
     * 执行 WebSocket 建连逻辑。
     */
    @Override
    protected WebSocket execute0() {
        Request request = PIPELINE.build(spec());
        return client().newWebSocket(request, spec().wsListener());
    }
}
