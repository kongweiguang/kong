package io.github.kongweiguang.http.client.core;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static io.github.kongweiguang.core.lang.Assert.isTure;
import static io.github.kongweiguang.core.lang.Assert.notNull;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * 配置中心
 *
 * @author kongweiguang
 */
public final class Conf {
    //全局配置
    private static final Conf global = new Conf();

    /**
     * 获取全局配置
     *
     * @return 全局配置 {@link Conf}
     */
    public static Conf global() {
        return global;
    }


    //拦截器
    private List<Interceptor> interceptors;

    //分发器
    private Dispatcher dispatcher;

    //异步调用的线程池
    private Executor exec;

    //连接池配置
    private ConnectionPool connectionPool;

    //代理配置
    private Proxy proxy;
    private Authenticator proxyAuthenticator;
    private ProxySelector proxySelector;

    //ssl配置
    private boolean ssl;

    //超时时间
    private Timeout timeout;

    //日志拦截器
    private HttpLoggingInterceptor httpLoggingInterceptor;

    //事件监听
    private EventListener eventListener;

    //重定向
    private boolean followRedirects;

    //ssl重定向
    private boolean followSslRedirects;

    //cookieJar
    private CookieJar cookieJar;

    private Conf(final Conf conf) {
        this.interceptors = conf.interceptors();
        this.dispatcher = conf.dispatcher();
        this.exec = conf.exec();
        this.connectionPool = conf.connectionPool();
        this.proxy = conf.proxy();
        this.proxyAuthenticator = conf.proxyAuthenticator();
        this.ssl = conf.ssl();
        this.timeout = conf.timeout();
        this.httpLoggingInterceptor = conf.httpLoggingInterceptor();
        this.eventListener = conf.eventListener();
        this.followRedirects = conf.followRedirects();
        this.followSslRedirects = conf.followSslRedirects();
        this.cookieJar = conf.cookieJar();
    }

    private Conf() {
    }

    /**
     * 获取新的配置
     *
     * @return 新的配置 {@link Conf}
     */
    public static Conf of() {
        return new Conf(global());
    }

    /**
     * 设置ssl开关
     *
     * @param ssl 是否开启
     * @return 自身实例 {@link Conf}
     */
    public Conf ssl(final boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    /**
     * 获取ssl开关
     *
     * @return 是否开启
     */
    public boolean ssl() {
        return ssl;
    }

    /**
     * 设置异步执行的线程池
     *
     * @param executor 使用的线程池
     * @return 自身实例 {@link Conf}
     */
    public Conf exec(final Executor executor) {
        notNull(executor, "executor must not be null");

        this.exec = executor;
        return this;
    }

    /**
     * 获取异步执行的线程池
     *
     * @return 线程池
     */
    public Executor exec() {
        if (isNull(exec)) {
            exec(InnerUtil.exec());
        }

        return exec;
    }

    /**
     * 添加请求拦截器
     *
     * @param interceptor 拦截器
     * @return 自身实例 {@link Conf}
     */
    public Conf addInterceptor(final Interceptor interceptor) {
        if (nonNull(interceptor)) {

            if (isNull(interceptors)) {
                this.interceptors = new ArrayList<>();
            }

            interceptors.add(interceptor);
        }

        return this;
    }

    /**
     * 获取请求拦截器
     *
     * @return 请求拦截器列表，如果没有设置，则返回空列表。
     */
    public List<Interceptor> interceptors() {
        return interceptors;
    }

    /**
     * 设置分发器
     *
     * @param dispatcher 分发器
     * @return 自身实例 {@link Conf}
     */
    public Conf dispatcher(final Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }

    /**
     * 获取分发器
     *
     * @return 分发器，如果没有设置，则返回null。
     */
    public Dispatcher dispatcher() {
        return dispatcher;
    }

    /**
     * 设置链接池
     *
     * @param pool 链接池
     * @return 自身实例 {@link Conf}
     */
    public Conf connectionPool(final ConnectionPool pool) {
        this.connectionPool = pool;
        return this;
    }

    /**
     * 获取连接池
     *
     * @return 链接池，如果没有设置，则返回null。
     */
    public ConnectionPool connectionPool() {
        return connectionPool;
    }

    /**
     * 设置http链接代理
     *
     * @param type 代理类型
     * @param host 主机
     * @param port 端口
     * @return 自身实例 {@link Conf}
     */
    public Conf proxy(final Proxy.Type type, final String host, final int port) {
        notNull(type, "type must not be null");
        notNull(host, "host must not be null");
        isTure(port > 0, "port must > 0");

        this.proxy = new Proxy(type, new InetSocketAddress(host, port));
        return this;
    }

    /**
     * 设置http链接代理
     *
     * @param host 主机
     * @param port 端口
     * @return 自身实例 {@link Conf}
     */
    public Conf proxy(final String host, final int port) {
        return proxy(Proxy.Type.HTTP, host, port);
    }

    /**
     * 获取代理对象
     *
     * @return 代理对象，如果没有设置，则返回null。
     */
    public Proxy proxy() {
        return proxy;
    }

    /**
     * 设置代理的授权认证
     *
     * @param username 账号
     * @param password 密码
     * @return 自身实例 {@link Conf}
     */
    public Conf proxyAuthenticator(final String username, final String password) {
        notNull(username, "username must not be null");
        notNull(password, "password must not be null");

        this.proxyAuthenticator = (route, response) -> response.request()
                .newBuilder()
                .header(Header.proxy_authorization.v(),
                        Credentials.basic(username, password, StandardCharsets.UTF_8))
                .build();

        return this;
    }

    /**
     * 获取代理的授权认证器
     *
     * @return 代理的授权认证器，如果没有设置，则返回null。
     */
    public Authenticator proxyAuthenticator() {
        return proxyAuthenticator;
    }

    /**
     * 获取代理选择器
     *
     * @return 代理选择器，如果没有设置，则返回null。
     */
    public ProxySelector proxySelector() {
        return proxySelector;
    }

    /**
     * 设置代理选择器
     *
     * @param proxySelector 代理选择器 {@link ProxySelector}
     * @return 自身实例 {@link Conf}
     */
    public Conf proxySelector(final ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
        return this;
    }

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间 {@link Timeout}
     * @return 自身实例 {@link Conf}
     */
    public Conf timeout(final Timeout timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 获取超时时间
     *
     * @return 超时时间，如果没有设置，则返回null。
     */
    public Timeout timeout() {
        return timeout;
    }

    /**
     * 设置日志
     *
     * @param logger 日志器
     * @param level  日志级别
     * @return 自身实例 {@link Conf}
     */
    public Conf log(final ReqLog logger, final HttpLoggingInterceptor.Level level) {
        this.httpLoggingInterceptor = InnerUtil.httpLoggingInterceptor(logger, level);
        return this;
    }

    /**
     * 获取日志拦截器
     *
     * @return 日志拦截器，如果没有设置，则返回null。
     */
    public HttpLoggingInterceptor httpLoggingInterceptor() {
        return httpLoggingInterceptor;
    }

    /**
     * 获取事件监听
     *
     * @return 事件监听器 {@link EventListener}
     */
    public EventListener eventListener() {
        return eventListener;
    }

    /**
     * 设置事件监听
     *
     * @param eventListener 事件监听器 {@link EventListener}
     * @return 自身实例 {@link Conf}
     */
    public Conf eventListener(final EventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    /**
     * 是否重定向
     *
     * @return 是否重定向，如果没有设置，则返回false。
     */
    public boolean followRedirects() {
        return followRedirects;
    }

    /**
     * 设置重定向
     *
     * @param followRedirects 是否重定向
     * @return 自身实例 {@link Conf}
     */
    public Conf followRedirects(final boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    /**
     * ssl是否重定向
     *
     * @return 是否重定向，如果没有设置，则返回false。
     */
    public boolean followSslRedirects() {
        return followSslRedirects;
    }

    /**
     * ssl设置重定向
     *
     * @param followSslRedirects 是否重定向
     * @return 自身实例 {@link Conf}
     */
    public Conf followSslRedirects(final boolean followSslRedirects) {
        this.followSslRedirects = followSslRedirects;
        return this;
    }

    /**
     * 获取cookieJar
     *
     * @return cookieJar {@link CookieJar}
     */
    public CookieJar cookieJar() {
        return cookieJar;
    }

    /**
     * 设置cookieJar
     *
     * @return 自身实例 {@link Conf}
     */
    public Conf cookieJar(final CookieJar cookieJar) {
        this.cookieJar = cookieJar;
        return this;
    }
}
