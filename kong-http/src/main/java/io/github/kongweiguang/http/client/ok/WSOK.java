package io.github.kongweiguang.http.client.ok;

import io.github.kongweiguang.http.client.OK;
import io.github.kongweiguang.http.client.builder.WSReqBuilder;
import io.github.kongweiguang.http.client.ws.WSListener;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;

import java.util.concurrent.CompletableFuture;

/**
 * WebSocket请求发送器
 *
 * @author kongweiguang
 */
public class WSOK extends OK<WSReqBuilder, WebSocket> {

    private WSOK(WSReqBuilder reqBuilder, OkHttpClient client) {
        super(reqBuilder, client);
    }

    /**
     * <h2>发送WebSocket请求</h2>
     *
     * @param reqBuilder WebSocket请求参数 {@link WSReqBuilder}
     * @param client     OkHttpClient {@link OkHttpClient}
     * @return WebSocket {@link WebSocket}
     */
    public static CompletableFuture<WebSocket> ok(WSReqBuilder reqBuilder, OkHttpClient client) {
        return new WSOK(reqBuilder, client).ojbk();
    }

    private CompletableFuture<WebSocket> ojbk() {
        return CompletableFuture.supplyAsync(this::execute, reqBuilder().config().exec());
    }

    /**
     * 执行WebSocket请求
     *
     * @return WebSocket对象
     */
    @Override
    protected WebSocket execute() {
        return client().newWebSocket(request(), wsListener());
    }

    /**
     * 获取WebSocket监听器
     *
     * @return WebSocket监听器
     */
    private WSListener wsListener() {
        return reqBuilder().wsListener();
    }
}