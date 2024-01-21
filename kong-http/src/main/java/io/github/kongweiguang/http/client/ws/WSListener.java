package io.github.kongweiguang.http.client.ws;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.ReqBuilder;
import io.github.kongweiguang.http.client.Res;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static java.util.Optional.ofNullable;

/**
 * ws监听器
 *
 * @author kongweiguang
 */
public abstract class WSListener extends WebSocketListener {

    private WebSocket ws;

    @Override
    public void onOpen(final WebSocket webSocket, final Response response) {
        this.ws = webSocket;
        open(webSocket.request().tag(ReqBuilder.class), Res.of(response));
    }

    @Override
    public void onMessage(final WebSocket webSocket, final String text) {
        this.ws = webSocket;
        msg(webSocket.request().tag(ReqBuilder.class), text);
    }

    @Override
    public void onMessage(final WebSocket webSocket, final ByteString bytes) {
        this.ws = webSocket;
        msg(webSocket.request().tag(ReqBuilder.class), bytes.toByteArray());
    }

    @Override
    public void onFailure(final WebSocket webSocket, final Throwable t, final Response response) {
        this.ws = webSocket;
        fail(webSocket.request().tag(ReqBuilder.class), Res.of(response), t);
    }

    @Override
    public void onClosing(final WebSocket webSocket, final int code, final String reason) {
        this.ws = webSocket;
        closing(webSocket.request().tag(ReqBuilder.class), code, reason);
    }

    @Override
    public void onClosed(final WebSocket webSocket, final int code, final String reason) {
        this.ws = webSocket;
        closed(webSocket.request().tag(ReqBuilder.class), code, reason);
    }

    /**
     * 发送消息
     *
     * @param text 字符串类型
     * @return {@link WSListener}
     */
    public WSListener send(final String text) {

        ofNullable(ws).ifPresent(ws -> ws.send(text));

        return this;
    }

    /**
     * 发送消息
     *
     * @param bytes byte类型
     * @return {@link WSListener}
     */
    public WSListener send(final byte[] bytes) {

        ofNullable(ws).ifPresent(ws -> ws.send(ByteString.of(bytes)));

        return this;
    }

    /**
     * 关闭连接
     */
    public void close() {
        ofNullable(ws).ifPresent(WebSocket::cancel);
    }


    /**
     * 打开连接触发事件
     *
     * @param req {@link Req}
     * @param res {@link Res}
     */
    public void open(final ReqBuilder req, final Res res) {
    }

    /**
     * 收到消息触发事件
     *
     * @param req  请求信息 {@link Req}
     * @param text string类型响应数据 {@link String}
     */
    public void msg(final ReqBuilder req, final String text) {
    }


    /**
     * 收到消息触发事件
     *
     * @param req   请求信息 {@link Req}
     * @param bytes byte类型响应数据 {@link Byte}
     */
    public void msg(final ReqBuilder req, final byte[] bytes) {
    }

    /**
     * 失败触发事件
     *
     * @param req 请求信息 {@link Req}
     * @param res 响应信息 {@link Res}
     * @param t   异常信息 {@link Throwable}
     */
    public void fail(final ReqBuilder req, final Res res, final Throwable t) {
    }

    /**
     * 关闭触发事件
     *
     * @param req    请求信息 {@link Req}
     * @param code   状态码
     * @param reason 原因
     */
    public void closing(final ReqBuilder req, final int code, final String reason) {
    }

    /**
     * 关闭触发事件
     *
     * @param req    请求信息 {@link Req}
     * @param code   状态码
     * @param reason 原因
     */
    public void closed(final ReqBuilder req, final int code, final String reason) {
    }

}
