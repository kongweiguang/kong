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

    public static Conf of() {
        return new Conf();
    }

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

    public Conf ssl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    public boolean ssl() {
        return ssl;
    }

    public Conf exec(Executor executor) {
        notNull(executor, "executor 不能为空");
        this.exec = executor;
        return this;
    }

    public Executor exec() {
        return exec;
    }

    public Conf addInterceptor(Interceptor interceptor) {
        if (nonNull(interceptor)) {
            if (isNull(interceptors)) {
                this.interceptors = new ArrayList<>();
            }
            interceptors.add(interceptor);
        }
        return this;
    }

    public List<Interceptor> interceptors() {
        return interceptors;
    }

    public Conf dispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }

    public Conf connectionPool(ConnectionPool pool) {
        this.connectionPool = pool;
        return this;
    }

    public ConnectionPool connectionPool() {
        return connectionPool;
    }

    public Conf proxy(Proxy.Type type, String host, int port) {
        notNull(type, "type 不能为空");
        notNull(host, "host 不能为空");
        isTrue(port > 0, "port 必须大于 0");
        this.proxy = new Proxy(type, new InetSocketAddress(host, port));
        return this;
    }

    public Conf proxy(String host, int port) {
        return proxy(Proxy.Type.HTTP, host, port);
    }

    public Proxy proxy() {
        return proxy;
    }

    public Conf proxyAuthenticator(String username, String password) {
        notNull(username, "username 不能为空");
        notNull(password, "password 不能为空");
        this.proxyAuthenticator = (route, response) -> response.request().newBuilder()
                .header(Header.PROXY_AUTHORIZATION.v(), Credentials.basic(username, password, StandardCharsets.UTF_8))
                .build();
        return this;
    }

    public Authenticator proxyAuthenticator() {
        return proxyAuthenticator;
    }

    public ProxySelector proxySelector() {
        return proxySelector;
    }

    public Conf proxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
        return this;
    }

    public Conf timeout(Timeout timeout) {
        this.timeout = timeout;
        return this;
    }

    public Timeout timeout() {
        return timeout;
    }

    public Conf log(ReqLog logger, HttpLoggingInterceptor.Level level) {
        this.httpLoggingInterceptor = HttpClientUtil.httpLoggingInterceptor(logger, level);
        return this;
    }

    public HttpLoggingInterceptor httpLoggingInterceptor() {
        return httpLoggingInterceptor;
    }

    public EventListener eventListener() {
        return eventListener;
    }

    public Conf eventListener(EventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    public boolean followRedirects() {
        return followRedirects;
    }

    public Conf followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    public boolean followSslRedirects() {
        return followSslRedirects;
    }

    public Conf followSslRedirects(boolean followSslRedirects) {
        this.followSslRedirects = followSslRedirects;
        return this;
    }

    public CookieJar cookieJar() {
        return cookieJar;
    }

    public Conf cookieJar(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
        return this;
    }
}

