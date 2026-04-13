package io.github.kongweiguang.http.client.sse;

import io.github.kongweiguang.core.lang.Opt;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.spec.SseReqSpec;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

/**
 * sse请求监听
 *
 * @author kongweiguang
 */
public abstract class SseListener extends EventSourceListener {

    /**
     * 保存 es
     */
    public EventSource es;


    /**
     * 执行 on open 操作
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        this.es = eventSource;

        open(eventSource.request().tag(SseReqSpec.class), Res.of(response));
    }


    /**
     * 执行 on event 操作
     */
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        this.es = eventSource;

        event(eventSource.request().tag(SseReqSpec.class), SseEvent.of().id(id).type(type).data(data));
    }


    /**
     * 执行 on failure 操作
     */
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        this.es = eventSource;

        fail(eventSource.request().tag(SseReqSpec.class), Res.of(response), t);
    }


    /**
     * 执行 on closed 操作
     */
    @Override
    public void onClosed(EventSource eventSource) {
        this.es = eventSource;

        closed(eventSource.request().tag(SseReqSpec.class));
    }


    /**
     * 执行 close con 操作
     */
    public void closeCon() {

        Opt.of(es).ifPresent(EventSource::cancel);
    }


    /**
     * 执行 open 操作
     */
    public void open(SseReqSpec req, Res res) {
    }


    /**
     * 执行 event 操作
     */
    public abstract void event(SseReqSpec req, SseEvent msg);


    /**
     * 设置 failure callback
     */
    public void fail(SseReqSpec req, Res res, Throwable t) {
    }


    /**
     * 执行 closed 操作
     */
    public void closed(SseReqSpec req) {
    }

}
