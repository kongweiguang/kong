package io.github.kongweiguang.http.server.core;

import com.sun.net.httpserver.HttpExchange;
import io.github.kongweiguang.core.util.IOs;
import io.github.kongweiguang.http.client.core.Method;

import java.io.IOException;
import java.util.Collections;
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
public final class RestHands implements com.sun.net.httpserver.HttpHandler {

    private static final Map<String, Map<Method, HttpHandler>> rest_map = new ConcurrentHashMap<>();
    private static final Map<String, Map<Method, HttpHandler>> sse_map = new ConcurrentHashMap<>();
    private static final Map<String, Map<Method, HttpHandler>> static_map = new ConcurrentHashMap<>();
    private static final Map<String, ReqType> path_type = new ConcurrentHashMap<>();

    /**
     * 创建一个RestHandler
     *
     * @return RestHandler
     */
    public static RestHands of() {
        return new RestHands();
    }

    /**
     * 添加rest接口
     *
     * @param path    路径
     * @param handler 处理器
     */
    public static void add(final ReqType type, final String path, final HttpHandler handler) {
        for (Method method : Method.values()) {
            add(type, method, path, handler);
        }
    }

    /**
     * 添加接口
     *
     * @param method  方法
     * @param path    路径
     * @param handler 处理器
     */
    public static void add(final ReqType type, final Method method, final String path, final HttpHandler handler) {
        path_type.put(path, type);
        handlerMap(type).computeIfAbsent(path, k -> new ConcurrentHashMap<>()).put(method, handler);
    }

    /**
     * 获取处理器
     *
     * @param type 类型
     * @return 处理器
     */
    private static Map<String, Map<Method, HttpHandler>> handlerMap(final ReqType type) {
        switch (type) {
            case REST:
                return rest_map;
            case SSE:
                return sse_map;
            case STATIC:
                return static_map;
        }
        return Collections.emptyMap();
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
     */
    @Override
    public void handle(final HttpExchange he) throws IOException {
        final String path = he.getRequestURI().getPath();
        final ReqType type = ofNullable(path_type.get(path)).orElse(ReqType.REST);
        try {
            final HttpHandler handler = ofNullable(handlerMap(type).get(path))
                    .map(e -> e.get(Method.valueOf(he.getRequestMethod())))
                    .orElse(null);
            if (nonNull(handler)) {
                handler0(he, handler);
            }
        } finally {
            if (!ReqType.SSE.equals(type)) {
                IOs.close(he);
            }
        }

    }

}
