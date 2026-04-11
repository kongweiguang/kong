package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.ResultHandler;
import io.github.kongweiguang.http.client.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.retry.RetryPolicy;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * WebSocket 请求执行器。
 *
 * @author kongweiguang
 */
public final class WSExecutor extends AbstractExecutor<WebSocket> {


    /**
     * 定义 pipeline constant
     */
    private static final RequestPipeline PIPELINE = new RequestPipeline();


    /**
     * 创建 WSExecutor instance
     */
    public WSExecutor(HttpRequestSpec spec, OkHttpClient client, RetryPolicy<WebSocket> retryPolicy, ResultHandler<WebSocket> handler) {
        super(spec, client, retryPolicy, handler);
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

