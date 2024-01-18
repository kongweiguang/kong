package io.github.kongweiguang.http.server.core;

import com.sun.net.httpserver.HttpExchange;
import io.github.kongweiguang.http.client.core.Method;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.kongweiguang.http.server.core.InnerUtil._404;
import static java.util.Objects.nonNull;

/**
 * rest处理器
 *
 * @author kongweiguang
 */
public final class RestHandler implements com.sun.net.httpserver.HttpHandler {

    private static final Map<Method, Map<String, HttpHandler>> rest_map = new ConcurrentHashMap<>();
    private static final Map<String, HttpHandler> defalut_map = new ConcurrentHashMap<>();

    public static void add(final String path, final HttpHandler handler) {
        defalut_map.put(path, handler);
    }

    public static void add(final Method method, final String path, final HttpHandler handler) {
        rest_map.computeIfAbsent(method, k -> new ConcurrentHashMap<>()).put(path, handler);
    }

    /**
     * 处理器
     *
     * @param he      HttpExchange
     * @param handler 处理器
     * @throws IOException 异常
     */
    private static void handler0(final HttpExchange he, final HttpHandler handler) throws IOException {
        if (_404(he, handler)) {
            return;
        }

        handler.doHandler(new HttpReq(he), new HttpRes(he));
    }

    /**
     * rest处理器
     *
     * @param he HttpExchange
     * @throws IOException
     */
    @Override
    public void handle(final HttpExchange he) throws IOException {
        final Method method = Method.valueOf(he.getRequestMethod());

        final Map<String, HttpHandler> map = rest_map.getOrDefault(method, defalut_map);
        HttpHandler handler = map.get(he.getRequestURI().getPath());

        if (nonNull(handler)) {
            handler0(he, handler);
        } else {
            if (Method.GET.equals(method)) {
                handler0(he, defalut_map.get(WebHandler.PATH));
            }
        }

    }


}
