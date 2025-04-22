package io.github.kongweiguang.http.server.core;

import com.sun.net.httpserver.HttpExchange;
import io.github.kongweiguang.core.lang.IOs;
import io.github.kongweiguang.http.client.core.Method;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.kongweiguang.http.server.core.InnerUtil._404;
import static java.util.Optional.ofNullable;

/**
 * rest处理器
 *
 * @author kongweiguang
 */
public class CenterHandler implements com.sun.net.httpserver.HttpHandler {

    private static final Map<String, Map<Method, HttpHandler>> rest_map = new ConcurrentHashMap<>();
    private static final Map<String, Map<Method, HttpHandler>> sse_map = new ConcurrentHashMap<>();
    private static final Map<String, Map<Method, HttpHandler>> static_map = new ConcurrentHashMap<>();
    private static final Map<String, ReqType> path_type = new ConcurrentHashMap<>();

    /**
     * 创建一个RestHandler
     *
     * @return RestHandler
     */
    public static CenterHandler of() {
        return new CenterHandler();
    }

    /**
     * 添加rest接口
     *
     * @param path    路径
     * @param handler 处理器
     */
    public static void add(ReqType type, String path, HttpHandler handler) {
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
    public static void add(ReqType type, Method method, final String path, final HttpHandler handler) {
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
        return switch (type) {
            case REST -> rest_map;
            case SSE -> sse_map;
            case STATIC -> static_map;
        };
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
            // 首先尝试精确路径匹配
            HttpHandler handler = ofNullable(handlerMap(type).get(path))
                    .map(e -> e.get(Method.valueOf(he.getRequestMethod())))
                    .orElse(null);
            
            // 如果精确匹配未找到处理器，且不是静态资源请求类型，尝试静态资源前缀匹配
            if (handler == null && type != ReqType.STATIC) {
                // 检查是否有静态资源路径前缀匹配
                for (String staticPath : static_map.keySet()) {
                    if (path.startsWith(staticPath)) {
                        // 找到匹配的静态资源前缀
                        handler = ofNullable(static_map.get(staticPath))
                                .map(e -> e.get(Method.valueOf(he.getRequestMethod())))
                                .orElse(null);
                        break;
                    }
                }
            }
    
         handler0(he, handler);
        } finally {
            if (!ReqType.SSE.equals(type)) {
                IOs.close(he);
            }
        }
    }

}
