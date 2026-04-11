package io.github.kongweiguang.http.client.sse;

import io.github.kongweiguang.core.lang.Opt;
import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.common.sse.SseEvent;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * sse请求监听
 *
 * @author kongweiguang
 */
public abstract class SSEListener extends EventSourceListener {

    public EventSource es;

    @Override
    /**
     * 连接建立成功时触发。
     */
    public void onOpen(EventSource eventSource, Response response) {
        this.es = eventSource;
        open(eventSource.request().tag(HttpRequestSpec.class), Res.of(response));
    }

    @Override
    /**
     * 收到 SSE 事件时触发。
     */
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        this.es = eventSource;
        event(eventSource.request().tag(HttpRequestSpec.class), SseEvent.of().id(id).type(type).data(data));
    }

    @Override
    /**
     * 处理执行失败后的回调。
     */
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        this.es = eventSource;
        fail(eventSource.request().tag(HttpRequestSpec.class), Res.of(response), t);
    }

    @Override
    /**
     * 连接关闭后触发。
     */
    public void onClosed(EventSource eventSource) {
        this.es = eventSource;
        closed(eventSource.request().tag(HttpRequestSpec.class));
    }

    /**
     * 关闭当前连接
     */
    public void closeCon() {
        Opt.of(es).ifPresent(EventSource::cancel);
    }

    /**
     * 打开连接触发事件
     *
     * @param req 请求信息 {@link Req}
     * @param res 响应信息 {@link Res}
     */
    public void open(HttpRequestSpec req, Res res) {
    }

    /**
     * 获取消息触发事件
     *
     * @param req 请求信息 {@link Req}
     * @param msg 事件信息 {@link SseEvent}
     */
    public abstract void event(HttpRequestSpec req, SseEvent msg);

    /**
     * 失败时触发事件
     *
     * @param req 请求信息 {@link Req}
     * @param res 响应信息 {@link Res}
     * @param t   异常信息 {@link Throwable}
     */
    public void fail(HttpRequestSpec req, Res res, Throwable t) {
    }

    /**
     * 关闭时出发连接
     *
     * @param req 请求信息 {@link Req}
     */
    public void closed(HttpRequestSpec req) {
    }

}
