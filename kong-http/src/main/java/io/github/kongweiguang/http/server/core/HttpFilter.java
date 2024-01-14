package io.github.kongweiguang.http.server.core;

import java.io.IOException;

@FunctionalInterface
public interface HttpFilter {

    void doFilter(final HttpReq req, final HttpRes res, final com.sun.net.httpserver.Filter.Chain chain) throws IOException;

    default String description() {
        return "default";
    }

}
