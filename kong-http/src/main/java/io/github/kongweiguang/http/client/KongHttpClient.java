package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Method;
import io.github.kongweiguang.http.client.core.Client;
import io.github.kongweiguang.http.client.core.ReqType;
import io.github.kongweiguang.http.client.executor.HttpExecutor;
import io.github.kongweiguang.http.client.executor.SSEExecutor;
import io.github.kongweiguang.http.client.executor.WSExecutor;
import io.github.kongweiguang.http.client.retry.HttpRetryPolicy;
import io.github.kongweiguang.http.client.retry.NoRetryPolicy;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.sse.EventSource;

import java.util.concurrent.CompletableFuture;

/**
 * kong-http 的统一客户端门面。
 */
public final class KongHttpClient {

    /**
     * 创建 KongHttpClient instance
     */
    private KongHttpClient() {
    }



    /**
     * 处理 execute 数据
     */
    public static CompletableFuture<Res> execute(HttpRequestSpec spec) {
        return execute(spec, Client.of(spec.conf()));
    }


    /**
     * 处理 execute 数据
     */
    public static CompletableFuture<Res> execute(HttpRequestSpec spec, OkHttpClient client) {
        ResultHandler<Res> handler = toHandler(spec);
        return new HttpExecutor(spec, client, new HttpRetryPolicy(spec.retry()), handler).executeAsync();
    }


    /**
     * 处理 execute blocking 数据
     */
    public static Res executeBlocking(HttpRequestSpec spec) {
        return execute(spec).join();
    }


    /**
     * 处理 execute sse 数据
     */
    public static CompletableFuture<EventSource> executeSse(HttpRequestSpec spec) {
        return new SSEExecutor(spec, Client.of(spec.conf()), new NoRetryPolicy<>(), ResultHandler.noop()).executeAsync();
    }


    /**
     * 处理 execute ws 数据
     */
    public static CompletableFuture<WebSocket> executeWs(HttpRequestSpec spec) {
        return new WSExecutor(spec, Client.of(spec.conf()), new NoRetryPolicy<>(), ResultHandler.noop()).executeAsync();
    }


    /**
     * 处理 to handler 数据
     */
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

