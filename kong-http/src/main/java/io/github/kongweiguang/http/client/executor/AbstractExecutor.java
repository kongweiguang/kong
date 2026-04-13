package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;
import io.github.kongweiguang.http.client.retry.RequestRetryExecutor;
import io.github.kongweiguang.http.client.spec.ReqSpec;
import okhttp3.OkHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * HTTP/SSE/WS 共用的执行模板。
 *
 * @author kongweiguang
 */
public abstract class AbstractExecutor<S extends ReqSpec<?, ?>, R> {

    /**
     * 保存 spec
     */
    private final S spec;

    /**
     * 保存 client
     */
    private final OkHttpClient client;

    /**
     * 保存 retry executor
     */
    private final RequestRetryExecutor<R> retryExecutor;


    /**
     * 创建 AbstractExecutor instance
     */
    protected AbstractExecutor(S builder, OkHttpClient client, RequestRetryExecutor<R> retryExecutor) {
        this.spec = builder;
        this.client = client;
        this.retryExecutor = retryExecutor;
    }

    /**
     * 返回 execute blocking
     */
    public final R executeBlocking() {
        try {
            return retryExecutor.execute(this::execute0);
        } catch (Throwable error) {
            throw new KongHttpRuntimeException(error);
        }
    }

    /**
     * 返回 execute async
     */
    public final CompletableFuture<R> executeAsync() {
        return CompletableFuture.supplyAsync(this::executeBlocking, spec.conf().exec());
    }

    /**
     * 返回 spec
     */
    protected S spec() {
        return spec;
    }


    /**
     * 返回 client
     */
    protected OkHttpClient client() {
        return client;
    }


    /**
     * 返回 execute core
     */
    protected abstract R execute0() throws Exception;

}
