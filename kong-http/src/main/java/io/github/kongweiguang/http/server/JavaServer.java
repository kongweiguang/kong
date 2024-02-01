package io.github.kongweiguang.http.server;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import io.github.kongweiguang.http.client.core.Method;
import io.github.kongweiguang.http.server.core.HttpFilter;
import io.github.kongweiguang.http.server.core.HttpHandler;
import io.github.kongweiguang.http.server.core.HttpReq;
import io.github.kongweiguang.http.server.core.HttpRes;
import io.github.kongweiguang.http.server.core.RestHandler;
import io.github.kongweiguang.http.server.core.WebHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.http.client.core.Method.DELETE;
import static io.github.kongweiguang.http.client.core.Method.GET;
import static io.github.kongweiguang.http.client.core.Method.POST;
import static io.github.kongweiguang.http.client.core.Method.PUT;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * 基于内置httpserver封装的简易http服务器
 *
 * @author kongweiguang
 */
public final class JavaServer {

    private final List<Filter> filters = new ArrayList<>();
    private final HttpServer httpServer;

    private JavaServer(final HttpsConfigurator config) {
        try {
            if (nonNull(config)) {
                final HttpsServer server = HttpsServer.create();
                server.setHttpsConfigurator(config);
                this.httpServer = server;
            } else {
                this.httpServer = HttpServer.create();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建一个JavaServer实例
     *
     * @return JavaServer实例
     */
    public static JavaServer of() {
        return new JavaServer(null);
    }

    /**
     * 创建一个JavaServer实例，添加https的配置
     *
     * @param config http配置 {@link HttpsConfigurator}
     * @return 当前对象
     */
    public static JavaServer of(final HttpsConfigurator config) {
        return new JavaServer(config);
    }

    /**
     * 设置http的线程池
     *
     * @param executor 线程池 {@link Executor}
     * @return 当前对象
     */
    public JavaServer executor(final Executor executor) {
        ofNullable(executor).ifPresent(e -> server().setExecutor(e));
        return this;
    }

    /**
     * 添加静态资源目的，并设置默认文件（默认是index.html）
     *
     * @param path     路径
     * @param fileName 文件名称
     * @return 当前对象
     */
    public JavaServer web(final String path, final String... fileName) {
        RestHandler.add(GET, WebHandler.PATH, new WebHandler(path, fileName.length > 1 ? fileName[0] : null));
        return this;
    }

    /**
     * 添加过滤器
     *
     * @param filter 过滤器 {@link HttpFilter}
     * @return 当前对象
     */
    public JavaServer filter(final HttpFilter filter) {
        notNull(filter, "filter must not be null");

        filters.add(new Filter() {
            @Override
            public void doFilter(final HttpExchange exchange, final com.sun.net.httpserver.Filter.Chain chain) throws IOException {
                filter.doFilter(new HttpReq(exchange), new HttpRes(exchange), chain);
            }

            @Override
            public String description() {
                return filter.description();
            }
        });
        return this;
    }

    /**
     * 添加restful接口
     *
     * @param method  方法 {@link Method}
     * @param path    路径
     * @param handler 处理器 {@link HttpHandler}
     * @return
     */
    public JavaServer rest(final Method method, final String path, final HttpHandler handler) {
        RestHandler.add(method, path, handler);
        return this;
    }

    /**
     * 添加restful接口
     *
     * @param path    路径
     * @param handler 处理器 {@link HttpHandler}
     * @return 当前对象
     */
    public JavaServer rest(final String path, final HttpHandler handler) {
        RestHandler.add(path, handler);
        return this;
    }

    /**
     * 添加get接口
     *
     * @param path    路径
     * @param handler 处理器 {@link HttpHandler}
     * @return 当前对象
     */
    public JavaServer get(final String path, final HttpHandler handler) {
        RestHandler.add(GET, path, handler);
        return this;
    }

    /**
     * 添加post接口
     *
     * @param path    路径
     * @param handler 处理器 {@link HttpHandler}
     * @return 当前对象
     */
    public JavaServer post(final String path, final HttpHandler handler) {
        RestHandler.add(POST, path, handler);
        return this;
    }

    /**
     * 添加delete接口
     *
     * @param path    路径
     * @param handler 处理器 {@link HttpHandler}
     * @return 当前对象
     */
    public JavaServer delete(final String path, final HttpHandler handler) {
        RestHandler.add(DELETE, path, handler);
        return this;
    }

    /**
     * 添加put接口
     *
     * @param path    路径
     * @param handler 处理器 {@link HttpHandler}
     * @return 当前对象
     */
    public JavaServer put(final String path, final HttpHandler handler) {
        RestHandler.add(PUT, path, handler);
        return this;
    }

    /**
     * 启动服务器
     *
     * @param port 端口
     */
    public void ok(int port) {
        start(new InetSocketAddress(port));
    }


    /**
     * 启动服务
     *
     * @param address 地址 {@link InetSocketAddress}
     */
    public void ok(InetSocketAddress address) {
        start(address);
    }


    private void start(final InetSocketAddress address) {
        try {
            final long start = System.currentTimeMillis();

            server().bind(address, 0);

            addContext();

            server().start();

            print(start);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addContext() {
        final HttpContext context = server().createContext("/", new RestHandler());

        if (!filters.isEmpty()) {
            context.getFilters().addAll(filters);
        }
    }

    private void print(long start) {
        final long cur = System.currentTimeMillis();

        System.err.printf(
                "[%s]KHTTP Server listen on 【%s:%s】 use time %dms %n",
                String.format("%tF %<tT", cur),
                server().getAddress().getHostName(),
                server().getAddress().getPort(),
                (cur - start)
        );
    }

    /**
     * 关闭服务
     *
     * @param delay 延迟时间
     */
    public void stop(int delay) {
        server().stop(delay);
    }

    //get

    public HttpServer server() {
        return httpServer;
    }

}
