package io.github.kongweiguang.http.server;

import com.sun.net.httpserver.*;
import io.github.kongweiguang.http.client.core.Method;
import io.github.kongweiguang.http.server.core.HttpHandler;
import io.github.kongweiguang.http.server.core.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.github.kongweiguang.http.client.core.Method.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * 基于内置httpserver封装的简易http服务器
 *
 * @author kongweiguang
 */
public final class JavaServer {

    private final List<Filter> filters = new ArrayList<>();
    private HttpServer httpServer;
    private HttpsConfigurator config;
    private Executor executor;


    private JavaServer() {
    }

    public static JavaServer of() {
        return new JavaServer();
    }

    public JavaServer httpsConfig(final HttpsConfigurator config) {
        this.config = config;
        return this;
    }

    public JavaServer executor(final Executor executor) {
        this.executor = executor;
        return this;
    }

    public JavaServer web(final String path, final String... fileName) {
        RestHandler.add(WebHandler.PATH,
                new WebHandler(path, fileName.length > 1 ? fileName[0] : null));
        return this;
    }

    public JavaServer filter(final HttpFilter filter) {
        filters().add(new Filter() {
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

    public JavaServer rest(final Method method, final String path, final io.github.kongweiguang.http.server.core.HttpHandler handler) {
        RestHandler.add(method, path, handler);
        return this;
    }

    public JavaServer rest(final String path, final HttpHandler handler) {
        RestHandler.add(path, handler);
        return this;
    }

    public JavaServer get(final String path, final HttpHandler handler) {
        RestHandler.add(GET, path, handler);
        return this;
    }

    public JavaServer post(final String path, final HttpHandler handler) {
        RestHandler.add(POST, path, handler);
        return this;
    }

    public JavaServer delete(final String path, final HttpHandler handler) {
        RestHandler.add(DELETE, path, handler);
        return this;
    }

    public JavaServer put(final String path, final HttpHandler handler) {
        RestHandler.add(PUT, path, handler);
        return this;
    }

    public void ok(int port) {
        start(new InetSocketAddress(port));
    }


    public void ok(InetSocketAddress address) {
        start(address);
    }


    private void start(final InetSocketAddress address) {
        try {
            final long start = System.currentTimeMillis();

            init();

            server().setExecutor(executor());

            server().bind(address, 0);

            addContext();

            server().start();

            print(start);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        if (nonNull(config())) {
            final HttpsServer server = HttpsServer.create();
            server.setHttpsConfigurator(config());
            this.httpServer = server;
        } else {
            this.httpServer = HttpServer.create();
        }
    }

    private void addContext() {
        server()
                .createContext("/", new RestHandler())
                .getFilters()
                .addAll(filters());
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

    public void stop(int delay) {
        server().stop(delay);
    }

    //get

    public HttpServer server() {
        return httpServer;
    }

    public List<Filter> filters() {
        return filters;
    }

    public HttpsConfigurator config() {
        return config;
    }

    public Executor executor() {
        if (isNull(executor)) {
            this.executor = Executors.newCachedThreadPool();
        }

        return executor;
    }
}
