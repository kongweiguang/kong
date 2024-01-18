package io.github.kongweiguang.http.server.core;

import com.sun.net.httpserver.Filter;

import java.io.IOException;

/**
 * http过滤器
 *
 * @author kongweiguang
 */
@FunctionalInterface
public interface HttpFilter {

    void doFilter(final HttpReq req, final HttpRes res, final Filter.Chain chain) throws IOException;

    default String description() {
        return "default";
    }

}
