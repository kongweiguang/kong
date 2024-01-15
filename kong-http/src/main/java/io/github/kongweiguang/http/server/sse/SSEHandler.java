package io.github.kongweiguang.http.server.sse;


import io.github.kongweiguang.http.client.core.ContentType;
import io.github.kongweiguang.http.client.core.Header;
import io.github.kongweiguang.http.client.sse.SseEvent;
import io.github.kongweiguang.http.server.core.HttpHandler;
import io.github.kongweiguang.http.server.core.HttpReq;
import io.github.kongweiguang.http.server.core.HttpRes;

import java.io.IOException;
import java.io.PrintWriter;

import static java.util.Objects.nonNull;

/**
 * sse的处理器
 *
 * @author kongweiguang
 * @since 0.1
 */
public abstract class SSEHandler implements HttpHandler {

    @Override
    public void doHandler(final HttpReq req, final HttpRes res) throws IOException {
        res.contentType(ContentType.event_stream.v());
        res.header(Header.cache_control.v(), "no-cache");
        res.header(Header.connection.v(), "keep-alive");
        res.sendOk();
        handler(req, res);
    }

    /**
     * 处理请求
     *
     * @param request  请求对象
     * @param response 响应对象
     * @throws IOException IO异常
     */
    public abstract void handler(final HttpReq req, final HttpRes res);

    /**
     * 发送数据给客户端
     *
     * @param res   输出流 {@link HttpRes }
     * @param event 数据对象 {@link SseEvent}
     * @return this
     */
    public SSEHandler send(final HttpRes res, final SseEvent event) {
        if (nonNull(res)) {
            final PrintWriter writer = res.writer();
            writer.write(event.toString());
            writer.flush();
        }
        return this;
    }

    /**
     * 关闭输出流
     *
     * @param res 输出流  {@link HttpRes }
     */
    public void close(final HttpRes res) {
        res.close();
    }
}
