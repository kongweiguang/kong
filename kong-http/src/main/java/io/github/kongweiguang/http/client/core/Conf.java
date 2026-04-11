package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.common.core.Header;
import io.github.kongweiguang.http.common.utils.HttpClientUtil;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static io.github.kongweiguang.core.lang.Assert.isTrue;
import static io.github.kongweiguang.core.lang.Assert.notNull;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * 请求配置模型。
 */
public class Conf {
    private static final Conf global = new Conf();

    /**
     * 返回全局共享配置实例。
     */
    public static Conf global() {
        return global;
    }

    private List<Interceptor> interceptors;
    private Dispatcher dispatcher;
    private Executor exec;
    private ConnectionPool connectionPool;
    private Proxy proxy;
    private Authenticator proxyAuthenticator;
    private ProxySelector proxySelector;
    private boolean ssl = true;
    private Timeout timeout;
    private HttpLoggingInterceptor httpLoggingInterceptor;
    private EventListener eventListener;
    private boolean followRedirects = true;
    private boolean followSslRedirects = true;
    private CookieJar cookieJar;

    private Conf() {
    }

    /**
     * 创建默认实例。
     */
    public static Conf of() {
        return new Conf();
    }

    /**
     * 复制当前配置并返回新对象。
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
     * 设置ssl 参数并返回当前对象，便于链式调用。
     */
    public Conf ssl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    /**
     * 获取ssl 对应值。
     */
    public boolean ssl() {
        return ssl;
    }

    /**
     * 设置exec 参数并返回当前对象，便于链式调用。
     */
    public Conf exec(Executor executor) {
        notNull(executor, "executor 不能为空");
        this.exec = executor;
        return this;
    }

    /**
     * 获取exec 对应值。
     */
    public Executor exec() {
        return exec;
    }

    /**
     * 设置addInterceptor 参数并返回当前对象，便于链式调用。
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
     * 获取interceptors 对应值。
     */
    public List<Interceptor> interceptors() {
        return interceptors;
    }

    /**
     * 设置dispatcher 参数并返回当前对象，便于链式调用。
     */
    public Conf dispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }

    /**
     * 获取dispatcher 对应值。
     */
    public Dispatcher dispatcher() {
        return dispatcher;
    }

    /**
     * 设置connectionPool 参数并返回当前对象，便于链式调用。
     */
    public Conf connectionPool(ConnectionPool pool) {
        this.connectionPool = pool;
        return this;
    }

    /**
     * 获取connectionPool 对应值。
     */
    public ConnectionPool connectionPool() {
        return connectionPool;
    }

    /**
     * 设置proxy 参数并返回当前对象，便于链式调用。
     */
    public Conf proxy(Proxy.Type type, String host, int port) {
        notNull(type, "type 不能为空");
        notNull(host, "host 不能为空");
        isTrue(port > 0, "port 必须大于 0");
        this.proxy = new Proxy(type, new InetSocketAddress(host, port));
        return this;
    }

    /**
     * 设置proxy 参数并返回当前对象，便于链式调用。
     */
    public Conf proxy(String host, int port) {
        return proxy(Proxy.Type.HTTP, host, port);
    }

    /**
     * 获取proxy 对应值。
     */
    public Proxy proxy() {
        return proxy;
    }

    /**
     * 设置proxyAuthenticator 参数并返回当前对象，便于链式调用。
     */
    public Conf proxyAuthenticator(String username, String password) {
        notNull(username, "username 不能为空");
        notNull(password, "password 不能为空");
        this.proxyAuthenticator = (route, response) -> response.request().newBuilder()
                .header(Header.PROXY_AUTHORIZATION.v(), Credentials.basic(username, password, StandardCharsets.UTF_8))
                .build();
        return this;
    }

    /**
     * 获取proxyAuthenticator 对应值。
     */
    public Authenticator proxyAuthenticator() {
        return proxyAuthenticator;
    }

    /**
     * 获取proxySelector 对应值。
     */
    public ProxySelector proxySelector() {
        return proxySelector;
    }

    /**
     * 设置proxySelector 参数并返回当前对象，便于链式调用。
     */
    public Conf proxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
        return this;
    }

    /**
     * 设置timeout 参数并返回当前对象，便于链式调用。
     */
    public Conf timeout(Timeout timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 获取timeout 对应值。
     */
    public Timeout timeout() {
        return timeout;
    }

    /**
     * 设置log 参数并返回当前对象，便于链式调用。
     */
    public Conf log(ReqLog logger, HttpLoggingInterceptor.Level level) {
        this.httpLoggingInterceptor = HttpClientUtil.httpLoggingInterceptor(logger, level);
        return this;
    }

    /**
     * 创建 HTTP 日志拦截器。
     */
    public HttpLoggingInterceptor httpLoggingInterceptor() {
        return httpLoggingInterceptor;
    }

    /**
     * 获取eventListener 对应值。
     */
    public EventListener eventListener() {
        return eventListener;
    }

    /**
     * 设置eventListener 参数并返回当前对象，便于链式调用。
     */
    public Conf eventListener(EventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    /**
     * 获取followRedirects 对应值。
     */
    public boolean followRedirects() {
        return followRedirects;
    }

    /**
     * 设置followRedirects 参数并返回当前对象，便于链式调用。
     */
    public Conf followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    /**
     * 获取followSslRedirects 对应值。
     */
    public boolean followSslRedirects() {
        return followSslRedirects;
    }

    /**
     * 设置followSslRedirects 参数并返回当前对象，便于链式调用。
     */
    public Conf followSslRedirects(boolean followSslRedirects) {
        this.followSslRedirects = followSslRedirects;
        return this;
    }

    /**
     * 获取cookieJar 对应值。
     */
    public CookieJar cookieJar() {
        return cookieJar;
    }

    /**
     * 设置cookieJar 参数并返回当前对象，便于链式调用。
     */
    public Conf cookieJar(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
        return this;
    }
}

