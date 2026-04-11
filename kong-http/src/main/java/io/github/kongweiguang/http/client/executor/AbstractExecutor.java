package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.ResultHandler;
import io.github.kongweiguang.http.client.retry.RetryPolicy;
import io.github.kongweiguang.http.common.exception.KongHttpRuntimeException;
import okhttp3.OkHttpClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * HTTP/SSE/WS 共用的执行模板。
 */
public abstract class AbstractExecutor<R> {
    private final HttpRequestSpec spec;
    private final OkHttpClient client;
    private final RetryPolicy<R> retryPolicy;
    private final ResultHandler<R> handler;

    protected AbstractExecutor(HttpRequestSpec spec, OkHttpClient client, RetryPolicy<R> retryPolicy, ResultHandler<R> handler) {
        this.spec = spec;
        this.client = client;
        this.retryPolicy = retryPolicy;
        this.handler = handler;
    }

    public final CompletableFuture<R> executeAsync() {
        Executor executor = spec.conf() == null ? null : spec.conf().exec();
        // 当调用方未显式配置线程池时，回退到 CompletableFuture 默认执行器，避免空指针。
        if (executor == null) {
            return CompletableFuture.supplyAsync(this::executeBlocking);
        }
        return CompletableFuture.supplyAsync(this::executeBlocking, executor);
    }

    public final R executeBlocking() {
        try {
            R result = retryPolicy.execute(this::executeCore);
            handler.onSuccess(result);
            return result;
        } catch (Throwable error) {
            handler.onFailure(error);
            throw new KongHttpRuntimeException(error);
        }
    }

    protected HttpRequestSpec spec() { return spec; }
    protected OkHttpClient client() { return client; }

    protected abstract R executeCore() throws Exception;
}


