package io.github.kongweiguang.http.server;

import com.sun.net.httpserver.*;
import io.github.kongweiguang.http.client.core.Method;
import io.github.kongweiguang.http.server.core.*;
import io.github.kongweiguang.http.server.core.HttpHandler;
import io.github.kongweiguang.http.server.sse.SSEHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.http.client.core.Method.*;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * 基于内置httpserver封装的简易http服务器
 *
 * @author kongweiguang
 */
public class JavaServer {

    private final List<Filter> filters = new ArrayList<>();
    private final HttpServer httpServer;

    private JavaServer(final HttpsConfigurator config) {
        try {
            if (nonNull(config)) {
                HttpsServer server = HttpsServer.create();
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
     * 设置自定义错误页面
     * 
     * @param statusCode 状态码
     * @param content 错误页面内容
     * @return 当前对象
     */
    public JavaServer errorPage(int statusCode, String content) {
        InnerUtil.setErrorPage(statusCode, content);
        return this;
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
    public static JavaServer of(HttpsConfigurator config) {
        return new JavaServer(config);
    }

    /**
     * 设置http的线程池
     *
     * @param executor 线程池 {@link Executor}
     * @return 当前对象
     */
    public JavaServer executor(Executor executor) {
        ofNullable(executor).ifPresent(e -> server().setExecutor(e));
        return this;
    }

    /**
     * 添加静态资源目录，并设置默认文件（默认是index.html）
     *
     * @param path     请求前缀
     * @param filePath 静态资源路径
     * @param fileName 文件名称
     * @return 当前对象
     */
    public JavaServer web(final String path,final String filePath, final String fileName) {
        CenterHandler.add(ReqType.STATIC, GET,path, new StaticHandler(path, filePath,fileName));
        return this;
    }
    
    /**
     * 添加静态资源目录，并设置高级选项
     *
     * @param path         请求前缀
     * @param enableCache  是否启用缓存
     * @param cacheMaxAge  缓存时间(秒)
     * @param indexFile    默认文件名称
     * @return 当前对象
     */
    public JavaServer web(final String path,final String filePath, boolean enableCache, int cacheMaxAge, String indexFile) {
        StaticHandler handler = new StaticHandler(path,filePath, indexFile, enableCache, cacheMaxAge);
        CenterHandler.add(ReqType.STATIC, GET,path, handler);
        return this;
    }
    
    /**
     * 添加静态资源目录，使用高级WebHandler配置
     *
     * @param path     请求前缀
     * @param filePath 静态资源路径
     * @param config   WebHandler配置函数
     * @return 当前对象
     */
    public JavaServer web(final String path,final String filePath, Consumer<StaticHandler> config) {
        StaticHandler handler = new StaticHandler(path, filePath,null);
        config.accept(handler);
        CenterHandler.add(ReqType.STATIC, GET, path, handler);
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
     * 添加sse接口
     *
     * @param method  方法
     * @param path    路径
     * @param handler 处理器
     * @return 当前对象
     */
    public JavaServer sse(final Method method, final String path, final SSEHandler handler) {
        CenterHandler.add(ReqType.SSE, method, path, handler);
        return this;
    }

    /**
     * 添加sse接口
     *
     * @param path    路径
     * @param handler 处理器
     * @return 当前对象
     */
    public JavaServer sse(final String path, final SSEHandler handler) {
        CenterHandler.add(ReqType.SSE, path, handler);
        return this;
    }

    /**
     * 添加restful接口
     *
     * @param method  方法 {@link Method}
     * @param path    路径
     * @param handler 处理器 {@link HttpHandler}
     * @return 当前对象
     */
    public JavaServer rest(final Method method, final String path, final HttpHandler handler) {
        CenterHandler.add(ReqType.REST, method, path, handler);
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
        CenterHandler.add(ReqType.REST, path, handler);
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
        CenterHandler.add(ReqType.REST, GET, path, handler);
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
        CenterHandler.add(ReqType.REST, POST, path, handler);
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
        CenterHandler.add(ReqType.REST, DELETE, path, handler);
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
        CenterHandler.add(ReqType.REST, PUT, path, handler);
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
        final HttpContext context = server().createContext("/", CenterHandler.of());

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
