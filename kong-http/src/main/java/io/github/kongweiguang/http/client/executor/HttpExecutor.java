package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.ResultHandler;
import io.github.kongweiguang.http.client.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.retry.RetryPolicy;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 使用重试策略与请求流水线的 HTTP 执行器。
 */
public final class HttpExecutor extends AbstractExecutor<Res> {


    /**
     * 定义 pipeline constant
     */
    private static final RequestPipeline PIPELINE = new RequestPipeline();

    /**
     * 创建 HttpExecutor instance
     */
    public HttpExecutor(HttpRequestSpec spec, OkHttpClient client, RetryPolicy<Res> retryPolicy, ResultHandler<Res> handler) {
        super(spec, client, retryPolicy, handler);
    }


    /**
     * 返回 execute core
     */
    @Override
    protected Res executeCore() throws Exception {
        Request request = PIPELINE.build(spec());
        return Res.of(client().newCall(request).execute());
    }
}

