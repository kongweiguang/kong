package io.github.kongweiguang.http.server.core;

import com.sun.net.httpserver.HttpExchange;
import io.github.kongweiguang.http.client.core.Method;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.kongweiguang.http.server.core.InnerUtil._404;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * rest处理器
 *
 * @author kongweiguang
 */
public final class RestHandler implements com.sun.net.httpserver.HttpHandler {

    private static final Map<String, Map<Method, HttpHandler>> rest_map = new ConcurrentHashMap<>();

    /**
     * 添加rest接口
     *
     * @param path    路径
     * @param handler 处理器
     */
    public static void add(final String path, final HttpHandler handler) {
        for (Method method : Method.values()) {
            add(method, path, handler);
        }
    }

    /**
     * 添加接口
     *
     * @param method  方法
     * @param path    路径
     * @param handler 处理器
     */
    public static void add(final Method method, final String path, final HttpHandler handler) {
        rest_map.computeIfAbsent(path, k -> new ConcurrentHashMap<>()).put(method, handler);
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

        final HttpHandler handler = ofNullable(rest_map.get(he.getRequestURI().getPath())).map(e -> e.get(method)).orElse(null);

        if (nonNull(handler)) {
            handler0(he, handler);
        } else {
            if (Method.GET.equals(method)) {
                handler0(he, ofNullable(rest_map.get(WebHandler.PATH)).map(e -> e.get(Method.GET)).orElse(null));
            }
        }

    }

}
