package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.ResultHandler;
import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;
import io.github.kongweiguang.http.client.retry.RetryPolicy;
import okhttp3.OkHttpClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * HTTP/SSE/WS 共用的执行模板。
 *
 * @author kongweiguang
 */
public abstract class AbstractExecutor<R> {

    /**
     * 保存 spec
     */
    private final HttpRequestSpec spec;

    /**
     * 保存 client
     */
    private final OkHttpClient client;

    /**
     * 保存 retry policy
     */
    private final RetryPolicy<R> retryPolicy;

    /**
     * 保存 handler
     */
    private final ResultHandler<R> handler;


    /**
     * 创建 AbstractExecutor instance
     */
    protected AbstractExecutor(HttpRequestSpec spec, OkHttpClient client, RetryPolicy<R> retryPolicy, ResultHandler<R> handler) {
        this.spec = spec;
        this.client = client;
        this.retryPolicy = retryPolicy;
        this.handler = handler;
    }

    /**
     * 返回 execute async
     */
    public final CompletableFuture<R> executeAsync() {
        Executor executor = spec.conf() == null ? null : spec.conf().exec();
        if (executor == null) {
            return CompletableFuture.supplyAsync(this::executeBlocking);
        }
        return CompletableFuture.supplyAsync(this::executeBlocking, executor);
    }


    /**
     * 返回 execute blocking
     */
    public final R executeBlocking() {
        try {
            R result = retryPolicy.execute(this::execute0);
            handler.onSuccess(result);
            return result;
        } catch (Throwable error) {
            handler.onFailure(error);
            throw new KongHttpRuntimeException(error);
        }
    }


    /**
     * 返回 spec
     */
    protected HttpRequestSpec spec() {
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

