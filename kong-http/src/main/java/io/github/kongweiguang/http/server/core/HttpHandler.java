package io.github.kongweiguang.http.server.core;

import java.io.IOException;

@FunctionalInterface
public interface HttpHandler {

    void doHandler(final HttpReq req, final HttpRes res) throws IOException;
}
