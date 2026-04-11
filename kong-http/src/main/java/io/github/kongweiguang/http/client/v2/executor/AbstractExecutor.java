package io.github.kongweiguang.http.client.v2.executor;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.v2.ResultHandler;
import io.github.kongweiguang.http.client.v2.retry.RetryPolicy;
import io.github.kongweiguang.http.common.exception.KongHttpRuntimeException;
import okhttp3.OkHttpClient;

import java.util.concurrent.CompletableFuture;

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
        return CompletableFuture.supplyAsync(this::executeBlocking, spec.conf().exec());
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


