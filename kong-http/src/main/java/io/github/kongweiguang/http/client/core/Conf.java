package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.consts.Header;
import io.github.kongweiguang.http.client.utils.HttpClientUtil;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.github.kongweiguang.core.lang.Assert.isTrue;
import static io.github.kongweiguang.core.lang.Assert.notNull;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * 请求配置模型。
 *
 * @author kongweiguang
 */
public class Conf {

    private static final Executor def = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 定义 global constant
     */
    private static final Conf global = new Conf();


    /**
     * 返回当前 conf
     */
    public static Conf global() {
        return global;
    }


    /**
     * 保存 interceptors list 数据
     */
    private List<Interceptor> interceptors;


    /**
     * 保存 dispatcher
     */
    private Dispatcher dispatcher;


    /**
     * 保存 exec
     */
    private Executor exec = def;

    /**
     * 保存 connection pool
     */
    private ConnectionPool connectionPool;


    /**
     * 保存 proxy
     */
    private Proxy proxy;


    /**
     * 保存 proxy authenticator
     */
    private Authenticator proxyAuthenticator;


    /**
     * 保存 proxy selector
     */
    private ProxySelector proxySelector;


    /**
     * 保存 ssl
     */
    private boolean ssl = true;


    /**
     * 保存 timeout
     */
    private Timeout timeout;


    /**
     * 保存 http logging interceptor
     */
    private HttpLoggingInterceptor httpLoggingInterceptor;


    /**
     * 保存 event listener
     */
    private EventListener eventListener;


    /**
     * 保存 follow redirects
     */
    private boolean followRedirects = true;


    /**
     * 保存 follow ssl redirects
     */
    private boolean followSslRedirects = true;


    /**
     * 保存 cookie jar
     */
    private CookieJar cookieJar;


    /**
     * 创建 Conf instance
     */
    private Conf() {
    }


    /**
     * 根据给定对象创建 wrapper instance
     */
    public static Conf of() {
        return new Conf();
    }


    /**
     * 返回当前 conf
     */
    public Conf copy() {
        Conf conf = new Conf();
        conf.interceptors = isNull(this.interceptors) ? null : new ArrayList<>(this.interceptors);
        conf.dispatcher = this.dispatcher;
        conf.exec = this.exec;
        conf.connectionPool = this.connectionPool;
        conf.proxy = this.proxy;
        conf.proxyAuthenticator = this.proxyAuthenticator;
        conf.proxySelector = this.proxySelector;
        conf.ssl = this.ssl;
        conf.timeout = this.timeout;
        conf.httpLoggingInterceptor = this.httpLoggingInterceptor;
        conf.eventListener = this.eventListener;
        conf.followRedirects = this.followRedirects;
        conf.followSslRedirects = this.followSslRedirects;
        conf.cookieJar = this.cookieJar;
        return conf;
    }


    /**
     * 设置 ssl
     */
    public Conf ssl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }


    /**
     * 返回 ssl
     */
    public boolean ssl() {
        return ssl;
    }


    /**
     * 设置 exec
     */
    public Conf exec(Executor executor) {
        notNull(executor, "executor must not be null");
        this.exec = executor;
        return this;
    }


    /**
     * 返回 exec
     */
    public Executor exec() {
        return exec;
    }


    /**
     * 添加 add interceptor 数据
     */
    public Conf addInterceptor(Interceptor interceptor) {
        if (nonNull(interceptor)) {
            if (isNull(interceptors)) {
                this.interceptors = new ArrayList<>();
            }
            interceptors.add(interceptor);
        }
        return this;
    }


    /**
     * 返回 interceptors
     */
    public List<Interceptor> interceptors() {
        return interceptors;
    }


    /**
     * 设置 dispatcher
     */
    public Conf dispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }


    /**
     * 返回 dispatcher
     */
    public Dispatcher dispatcher() {
        return dispatcher;
    }


    /**
     * 设置 connection pool
     */
    public Conf connectionPool(ConnectionPool pool) {
        this.connectionPool = pool;
        return this;
    }


    /**
     * 返回 connection pool
     */
    public ConnectionPool connectionPool() {
        return connectionPool;
    }


    /**
     * 设置 proxy
     */
    public Conf proxy(Proxy.Type type, String host, int port) {

        notNull(type, "type must not be null");

        notNull(host, "host must not be null");

        isTrue(port > 0, "port must be greater than 0");
        this.proxy = new Proxy(type, new InetSocketAddress(host, port));
        return this;
    }


    /**
     * 设置 proxy
     */
    public Conf proxy(String host, int port) {
        return proxy(Proxy.Type.HTTP, host, port);
    }


    /**
     * 返回 proxy
     */
    public Proxy proxy() {
        return proxy;
    }


    /**
     * 设置 proxy authenticator
     */
    public Conf proxyAuthenticator(String username, String password) {

        notNull(username, "username must not be null");

        notNull(password, "password must not be null");
        this.proxyAuthenticator = (route, response) -> response.request().newBuilder()
                .header(Header.PROXY_AUTHORIZATION.value(), Credentials.basic(username, password, StandardCharsets.UTF_8))

                .build();
        return this;
    }


    /**
     * 返回 proxy authenticator
     */
    public Authenticator proxyAuthenticator() {
        return proxyAuthenticator;
    }


    /**
     * 返回 proxy selector
     */
    public ProxySelector proxySelector() {
        return proxySelector;
    }


    /**
     * 设置 proxy selector
     */
    public Conf proxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
        return this;
    }


    /**
     * 设置 timeout
     */
    public Conf timeout(Timeout timeout) {
        this.timeout = timeout;
        return this;
    }


    /**
     * 返回 timeout
     */
    public Timeout timeout() {
        return timeout;
    }


    /**
     * 设置 log
     */
    public Conf log(ReqLog logger, HttpLoggingInterceptor.Level level) {
        this.httpLoggingInterceptor = HttpClientUtil.httpLoggingInterceptor(logger, level);
        return this;
    }


    /**
     * 创建指定 log level 的 HTTP logging interceptor
     */
    public HttpLoggingInterceptor httpLoggingInterceptor() {
        return httpLoggingInterceptor;
    }


    /**
     * 返回 event listener
     */
    public EventListener eventListener() {
        return eventListener;
    }


    /**
     * 设置 event listener
     */
    public Conf eventListener(EventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }


    /**
     * 返回 follow redirects
     */
    public boolean followRedirects() {
        return followRedirects;
    }


    /**
     * 设置 follow redirects
     */
    public Conf followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }


    /**
     * 返回 follow ssl redirects
     */
    public boolean followSslRedirects() {
        return followSslRedirects;
    }


    /**
     * 设置 follow ssl redirects
     */
    public Conf followSslRedirects(boolean followSslRedirects) {
        this.followSslRedirects = followSslRedirects;
        return this;
    }


    /**
     * 返回 cookie jar
     */
    public CookieJar cookieJar() {
        return cookieJar;
    }


    /**
     * 设置 cookie jar
     */
    public Conf cookieJar(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
        return this;
    }
}

