package io.github.kongweiguang.http.server.core;

import java.io.IOException;

/**
 * http处理器
 *
 * @author kongweiguang
 */
@FunctionalInterface
public interface HttpHandler {

    void doHandler(final HttpReq req, final HttpRes res) throws IOException;
}
