package io.github.kongweiguang.http.server.sse;


import io.github.kongweiguang.core.lang.Opt;
import io.github.kongweiguang.http.common.sse.SseEvent;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Header;
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

    public HttpRes httpRes;

    @Override
    public void doHandler(HttpReq req, HttpRes res) throws IOException {
        res.contentType(ContentType.EVENT_STREAM.v());
        res.header(Header.CACHE_CONTROL.v(), "no-cache");
        res.header(Header.CONNECTION.v(), "keep-alive");
        res.sendOk();
        this.httpRes = res;
        handler(req, res);
    }

    /**
     * 处理请求
     *
     * @param req 请求对象
     * @param res 响应对象
     * @throws IOException IO异常
     */
    public abstract void handler(HttpReq req, HttpRes res);

    /**
     * 发送数据给客户端
     *
     * @param event 数据对象 {@link SseEvent}
     * @return this
     */
    public SSEHandler send(SseEvent event) {
        Opt.ofNullable(httpRes)
                .ifPresent(r -> {
                    PrintWriter writer = r.writer();
                    writer.write(event.toString());
                    writer.flush();
                });

        return this;
    }

    /**
     * 关闭输出流
     *
     * @param res 输出流  {@link HttpRes }
     */
    public void close(final HttpRes res) {
        if (nonNull(res)) {
            res.close();
        }
    }
}
