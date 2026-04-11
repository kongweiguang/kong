package io.github.kongweiguang.http.client.ws;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.kongweiguang.core.lang.Opt.ofNullable;


/**
 * ws监听器
 *
 * @author kongweiguang
 */
public abstract class WSListener extends WebSocketListener {
    private static final Logger log = LoggerFactory.getLogger(WSListener.class);
    protected WebSocket ws;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.ws = webSocket;
        open(webSocket.request().tag(HttpRequestSpec.class), Res.of(response));
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        this.ws = webSocket;
        msg(webSocket.request().tag(HttpRequestSpec.class), text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        this.ws = webSocket;
        msg(webSocket.request().tag(HttpRequestSpec.class), bytes.toByteArray());
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        this.ws = webSocket;
        fail(webSocket.request().tag(HttpRequestSpec.class), Res.of(response), t);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        this.ws = webSocket;
        closing(webSocket.request().tag(HttpRequestSpec.class), code, reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        this.ws = webSocket;
        closed(webSocket.request().tag(HttpRequestSpec.class), code, reason);
    }

    /**
     * 发送消息
     *
     * @param text 字符串类型
     * @return {@link WSListener}
     */
    public WSListener send(String text) {
        return send(text.getBytes());
    }

    /**
     * 发送消息
     *
     * @param bytes byte类型
     * @return {@link WSListener}
     */
    public WSListener send(byte[] bytes) {

        ofNullable(ws).ifPresent(ws -> ws.send(ByteString.of(bytes)));

        return this;
    }

    /**
     * 关闭连接
     */
    public void closeCon() {
        ofNullable(ws).ifPresent(WebSocket::cancel);
    }


    /**
     * 打开连接触发事件
     *
     * @param req {@link Req}
     * @param res {@link Res}
     */
    public void open(HttpRequestSpec req, Res res) {
    }

    /**
     * 收到消息触发事件
     *
     * @param req  请求信息 {@link Req}
     * @param text string类型响应数据 {@link String}
     */
    public void msg(HttpRequestSpec req, String text) {
    }


    /**
     * 收到消息触发事件
     *
     * @param req   请求信息 {@link Req}
     * @param bytes byte类型响应数据 {@link Byte}
     */
    public void msg(HttpRequestSpec req, byte[] bytes) {
    }

    /**
     * 失败触发事件
     *
     * @param req 请求信息 {@link Req}
     * @param res 响应信息 {@link Res}
     * @param t   异常信息 {@link Throwable}
     */
    public void fail(HttpRequestSpec req, Res res, Throwable t) {
    }

    /**
     * 关闭触发事件
     *
     * @param req    请求信息 {@link Req}
     * @param code   状态码
     * @param reason 原因
     */
    public void closing(HttpRequestSpec req, int code, String reason) {
    }

    /**
     * 关闭触发事件
     *
     * @param req    请求信息 {@link Req}
     * @param code   状态码
     * @param reason 原因
     */
    public void closed(HttpRequestSpec req, int code, String reason) {
    }

}
