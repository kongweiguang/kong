package io.github.kongweiguang.http.server.core;

import java.io.IOException;

/**
 * http处理器
 *
 * @author kongweiguang
 */
@FunctionalInterface
public interface HttpHandler {

    void doHandler(HttpReq req, HttpRes res) throws IOException;
}
