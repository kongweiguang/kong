package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.core.Client;
import io.github.kongweiguang.http.client.core.ReqType;
import io.github.kongweiguang.http.client.ResultHandler;
import io.github.kongweiguang.http.client.executor.HttpExecutor;
import io.github.kongweiguang.http.client.executor.SSEExecutor;
import io.github.kongweiguang.http.client.executor.WSExecutor;
import io.github.kongweiguang.http.client.retry.HttpRetryPolicy;
import io.github.kongweiguang.http.client.retry.NoRetryPolicy;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Method;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.sse.EventSource;

import java.util.concurrent.CompletableFuture;

/**
 * kong-http 的统一客户端门面。
 */
public final class KongHttpClient {

    private KongHttpClient() {
    }

    public static HttpRequestSpec.Builder request(String url) {
        return new HttpRequestSpec.Builder().url(url).reqType(ReqType.http).method(Method.GET);
    }

    public static HttpRequestSpec.Builder ws(String url) {
        return new HttpRequestSpec.Builder().url(url).reqType(ReqType.ws).method(Method.GET);
    }

    public static HttpRequestSpec.Builder sse(String url) {
        return new HttpRequestSpec.Builder()
                .url(url)
                .reqType(ReqType.sse)
                .method(Method.GET)
                .contentType(ContentType.EVENT_STREAM.v());
    }

    public static CompletableFuture<Res> execute(HttpRequestSpec spec) {
        return execute(spec, Client.of(spec.conf()));
    }

    public static CompletableFuture<Res> execute(HttpRequestSpec spec, OkHttpClient client) {
        ResultHandler<Res> handler = toHandler(spec);
        return new HttpExecutor(spec, client, new HttpRetryPolicy(spec.retry()), handler).executeAsync();
    }

    public static Res executeBlocking(HttpRequestSpec spec) {
        return execute(spec).join();
    }

    public static CompletableFuture<EventSource> executeSse(HttpRequestSpec spec) {
        return new SSEExecutor(spec, Client.of(spec.conf()), new NoRetryPolicy<>(), ResultHandler.noop()).executeAsync();
    }

    public static CompletableFuture<WebSocket> executeWs(HttpRequestSpec spec) {
        return new WSExecutor(spec, Client.of(spec.conf()), new NoRetryPolicy<>(), ResultHandler.noop()).executeAsync();
    }

    private static ResultHandler<Res> toHandler(HttpRequestSpec spec) {
        return new ResultHandler<>() {
            @Override
            public void onSuccess(Res result) {
                if (spec.onSuccess() != null) {
                    spec.onSuccess().accept(result);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                if (spec.onFailure() != null) {
                    spec.onFailure().accept(error);
                }
            }
        };
    }
}


