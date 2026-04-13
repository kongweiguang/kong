package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.retry.RequestRetryExecutor;
import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 使用重试策略与请求流水线的 HTTP 执行器。
 *
 * @author kongweiguang
 */
public final class HttpExecutor extends AbstractExecutor<HttpReqSpec, Res> {

    private static final RequestPipeline<HttpReqSpec> PIPELINE = RequestPipeline.http();

    /**
     * 创建 HttpExecutor instance
     */
    public HttpExecutor(HttpReqSpec spec, OkHttpClient client, RequestRetryExecutor<Res> retryExecutor) {
        super(spec, client, retryExecutor);
    }

    /**
     * 返回 execute core
     */
    @Override
    protected Res execute0() throws Exception {
        Request request = PIPELINE.build(spec());
        return Res.of(client().newCall(request).execute());
    }
}
