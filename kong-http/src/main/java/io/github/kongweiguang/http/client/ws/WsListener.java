package io.github.kongweiguang.http.client.ws;

import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.spec.WsReqSpec;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static io.github.kongweiguang.core.lang.Opt.ofNullable;


/**
 * ws监听器
 *
 * @author kongweiguang
 */
public abstract class WsListener extends WebSocketListener {

    /**
     * 定义 WebSocket scheme name
     */
    protected WebSocket ws;

    /**
     * 执行 on open 操作
     */
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.ws = webSocket;

        open(webSocket.request().tag(WsReqSpec.class), Res.of(response));
    }

    /**
     * 执行 onMessage 操作
     */
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        this.ws = webSocket;

        msg(webSocket.request().tag(WsReqSpec.class), text);
    }

    /**
     * 执行 onMessage 操作
     */
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        this.ws = webSocket;

        msg(webSocket.request().tag(WsReqSpec.class), bytes.toByteArray());
    }


    /**
     * 执行 on failure 操作
     */
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        this.ws = webSocket;

        fail(webSocket.request().tag(WsReqSpec.class), Res.of(response), t);
    }


    /**
     * 执行 on closing 操作
     */
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        this.ws = webSocket;

        closing(webSocket.request().tag(WsReqSpec.class), code, reason);
    }


    /**
     * 执行 on closed 操作
     */
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        this.ws = webSocket;

        closed(webSocket.request().tag(WsReqSpec.class), code, reason);
    }


    /**
     * 设置 send
     */
    public WsListener send(String text) {
        return send(text.getBytes());
    }


    /**
     * 设置 send
     */
    public WsListener send(byte[] bytes) {


        ofNullable(ws).ifPresent(ws -> ws.send(ByteString.of(bytes)));

        return this;
    }


    /**
     * 执行 close con 操作
     */
    public void closeCon() {

        ofNullable(ws).ifPresent(WebSocket::cancel);
    }


    /**
     * 执行 open 操作
     */
    public void open(WsReqSpec req, Res res) {
    }


    /**
     * 执行 msg 操作
     */
    public void msg(WsReqSpec req, String text) {
    }


    /**
     * 执行 msg 操作
     */
    public void msg(WsReqSpec req, byte[] bytes) {
    }


    /**
     * 设置 failure callback
     */
    public void fail(WsReqSpec req, Res res, Throwable t) {
    }


    /**
     * 执行 closing 操作
     */
    public void closing(WsReqSpec req, int code, String reason) {
    }


    /**
     * 执行 closed 操作
     */
    public void closed(WsReqSpec req, int code, String reason) {
    }

}
