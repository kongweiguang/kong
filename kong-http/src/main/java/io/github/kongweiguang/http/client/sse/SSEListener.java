package io.github.kongweiguang.http.client.sse;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.ReqBuilder;
import io.github.kongweiguang.http.client.Res;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import static java.util.Objects.nonNull;

/**
 * sse请求监听
 *
 * @author kongweiguang
 */
public abstract class SSEListener extends EventSourceListener {

    public EventSource es;

    @Override
    public void onOpen(EventSource eventSource, Response response) {
        this.es = eventSource;
        open(eventSource.request().tag(ReqBuilder.class), Res.of(response));
    }

    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        this.es = eventSource;
        event(eventSource.request().tag(ReqBuilder.class), SseEvent.of().id(id).type(type).data(data));
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        this.es = eventSource;
        fail(eventSource.request().tag(ReqBuilder.class), Res.of(response), t);
    }

    @Override
    public void onClosed(EventSource eventSource) {
        this.es = eventSource;
        closed(eventSource.request().tag(ReqBuilder.class));
    }

    /**
     * 关闭当前连接
     */
    public void close() {
        if (nonNull(es)) {
            es.cancel();
        }
    }

    /**
     * 打开连接触发事件
     *
     * @param req 请求信息 {@link Req}
     * @param res 响应信息 {@link Res}
     */
    public void open(ReqBuilder req, final Res res) {
    }

    /**
     * 获取消息触发事件
     *
     * @param req 请求信息 {@link Req}
     * @param msg 事件信息 {@link SseEvent}
     */
    public abstract void event(final ReqBuilder req, final SseEvent msg);

    /**
     * 失败时触发事件
     *
     * @param req 请求信息 {@link Req}
     * @param res 响应信息 {@link Res}
     * @param t   异常信息 {@link Throwable}
     */
    public void fail(final ReqBuilder req, final Res res, final Throwable t) {
    }

    /**
     * 关闭时出发连接
     *
     * @param req 请求信息 {@link Req}
     */
    public void closed(final ReqBuilder req) {
    }

}
