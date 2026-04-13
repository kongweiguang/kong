package io.github.kongweiguang.http.client.spec;

import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.consts.*;
import io.github.kongweiguang.http.client.core.*;
import io.github.kongweiguang.json.Json;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.logging.HttpLoggingInterceptor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.isTrue;
import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.core.lang.Opt.ofNullable;
import static io.github.kongweiguang.http.client.utils.HttpClientUtil.httpLoggingInterceptor;
import static io.github.kongweiguang.http.client.utils.HttpClientUtil.parseUrlBuilder;
import static java.util.Objects.nonNull;

/**
 * http请求构建器
 *
 * @author kongweiguang
 */
@SuppressWarnings("unchecked")
public abstract class ReqSpec<T extends ReqSpec<T, R>, R> {

    protected ReqType reqType = ReqType.http;
    protected final Conf conf = Conf.global().copy();
    protected final Builder builder = new Builder();

    //header
    protected Method method = Method.GET;
    protected Map<String, String> cookieMap = new LinkedHashMap<>();
    protected String contentType;
    protected Charset charset = StandardCharsets.UTF_8;

    //url
    protected HttpUrl.Builder urlBuilder = parseUrlBuilder("http://localhost");

    //body
    protected byte[] body;

    //attachment
    protected Map<Object, Object> attachment = new HashMap<>();

    protected ReqSpec() {
    }

    /**
     * 请求的类型
     *
     * @param reqType 请求类型 {@link ReqType}
     * @return ReqBuilder {@link ReqSpec}
     */
    public T reqType(ReqType reqType) {
        this.reqType = reqType;
        return (T) this;
    }

    /**
     * 配置请求
     *
     * @param c 配置 {@link Conf}
     * @return ReqBuilder {@link ReqSpec}
     */
    public T config(Consumer<Conf> c) {
        notNull(c, "conf consumer must not be null");

        c.accept(conf);
        return (T) this;
    }

    /**
     * 禁用ssl校验
     *
     * @return ReqBuilder {@link ReqSpec}
     */
    public T disableSslValid() {
        conf.ssl(false);
        return (T) this;
    }

    /**
     * 禁用重定向
     *
     * @return ReqBuilder {@link ReqSpec}
     */
    public T disableRedirect() {
        conf.followRedirects(false);
        return (T) this;
    }

    /**
     * 禁用ssl重定向
     *
     * @return ReqBuilder {@link ReqSpec}
     */
    public T disableSslRedirect() {
        conf.followSslRedirects(false);
        return (T) this;
    }

    /**
     * 请求超时时间设置
     *
     * @param timeout 超时时间
     * @return ReqBuilder {@link ReqSpec}
     */
    public T timeout(Duration timeout) {
        return timeout(timeout, timeout, timeout);
    }

    /**
     * 请求超时时间设置
     *
     * @param connect 连接超时时间
     * @param write   写入超时时间
     * @param read    读取超时时间
     * @return ReqBuilder {@link ReqSpec}
     */
    public T timeout(Duration connect, Duration write, Duration read) {
        notNull(connect, "connect must not be null");
        notNull(write, "write must not be null");
        notNull(read, "read must not be null");

        conf.timeout(new Timeout(connect, write, read));

        return (T) this;
    }

    /**
     * 设置slf4j为日志器
     *
     * @param level 日志级别 {@link HttpLoggingInterceptor.Level}
     * @return ReqBuilder {@link ReqSpec}
     */
    public T slf4j(HttpLoggingInterceptor.Level level) {
        return log(ReqLog.slf4j, level);
    }

    /**
     * 设置日志器和日志级别
     *
     * @param logger 日志器
     * @param level  日志级别
     * @return ReqBuilder {@link ReqSpec}
     */
    public T log(ReqLog logger, HttpLoggingInterceptor.Level level) {
        notNull(logger, "logger must not be null");
        notNull(level, "level must not be null");

        conf.addInterceptor(httpLoggingInterceptor(logger, level));
        return (T) this;
    }

    /**
     * 请求中添加的附件
     *
     * @param k 键
     * @param v 值
     * @return ReqBuilder {@link ReqSpec}
     */
    public T attr(Object k, Object v) {
        attachment.put(k, v);
        return (T) this;
    }


    /**
     * 从附件中获取值
     *
     * @param k
     * @param <A>
     * @return
     */
    public <A> A attr(Object k) {
        return (A) attachment.get(k);
    }


    /**
     * 同步请求，使用全局配置
     *
     * @return Res {@link Res}
     */
    public R ok() {
        return execute(Client.of(conf));
    }

    /**
     * 异步请求
     *
     * @return Res {@link ReqSpec}
     **/
    public CompletableFuture<R> okAsync() {
        return CompletableFuture.supplyAsync(() -> execute(Client.of(conf)), conf.exec());
    }

    /**
     * 发送请求，自定义okhttpClient
     *
     * @return Res {@link ReqSpec}
     */
    protected abstract R execute(OkHttpClient client);

    /**
     * 设置method
     *
     * @param method {@link Method}
     * @return ReqBuilder {@link ReqSpec}
     */
    public T method(Method method) {
        notNull(method, "method must not be null");

        this.method = method;
        return (T) this;
    }


    /**
     * 设置url，默认请求根目录 <a href="http://localhost/">http://localhost</a>
     *
     * @param url 请求地址
     * @return ReqBuilder {@link ReqSpec}
     */
    public T url(String url) {
        notNull(url, "url must not be null");

        this.urlBuilder = parseUrlBuilder(url);
        return (T) this;
    }

    /**
     * 设置url的协议
     *
     * @param scheme 协议
     * @return ReqBuilder {@link ReqSpec}
     */
    public T scheme(String scheme) {
        notNull(scheme, "scheme must not be null");

        switch (scheme) {
            case Const.HTTP:
            case Const.HTTPS: {
                break;
            }
            case Const.WS: {
                scheme = Const.HTTP;
                break;
            }
            case Const.WSS: {
                scheme = Const.HTTPS;
                break;
            }
            default:
                throw new IllegalArgumentException("unexpected scheme : " + scheme);
        }

        urlBuilder.scheme(scheme);
        return (T) this;
    }

    /**
     * 设置url的主机地址
     *
     * @param host 主机地址
     * @return ReqBuilder {@link ReqSpec}
     */
    public T host(String host) {
        notNull(host, "host must not be null");

        urlBuilder.host(host);
        return (T) this;
    }

    /**
     * 设置url的端口
     *
     * @param port 端口
     * @return ReqBuilder {@link ReqSpec}
     */
    public T port(int port) {
        isTrue(port >= 1 && port <= 65535, "port must >= 1 && port <= 65535 ");

        urlBuilder.port(port);
        return (T) this;
    }

    /**
     * 设置url的path
     *
     * @param path 路径
     * @return ReqBuilder {@link ReqSpec}
     */
    public T path(String path) {
        notNull(path, "path must not be null");

        urlBuilder.addPathSegments(path);
        return (T) this;
    }

    /**
     * 设置url的query
     *
     * @param k 键
     * @param v 值
     * @return ReqBuilder {@link ReqSpec}
     */
    public T query(String k, Object v) {

        if (nonNull(k) && nonNull(v)) {
            urlBuilder.addQueryParameter(k, String.valueOf(v));
        }

        return (T) this;
    }

    /**
     * 设置url的query并编码
     *
     * @param k 键
     * @param v 值
     * @return ReqBuilder {@link ReqSpec}
     */
    public T encodeQuery(String k, Object v) {

        if (nonNull(k) && nonNull(v)) {
            urlBuilder.addEncodedQueryParameter(k, String.valueOf(v));
        }

        return (T) this;
    }

    /**
     * 设置url的query
     *
     * @param k  键
     * @param vs 值集合
     * @return ReqBuilder {@link ReqSpec}
     */
    public T query(String k, Iterable<Object> vs) {

        if (nonNull(k) && nonNull(vs)) {
            vs.forEach(v -> urlBuilder.addQueryParameter(k, String.valueOf(v)));
        }

        return (T) this;
    }

    /**
     * 设置url的query并编码
     *
     * @param k  键
     * @param vs 值集合
     * @return ReqBuilder {@link ReqSpec}
     */
    public T encodedQuery(String k, Iterable<Object> vs) {

        if (nonNull(k) && nonNull(vs)) {
            vs.forEach(v -> urlBuilder.addEncodedQueryParameter(k, String.valueOf(v)));
        }

        return (T) this;
    }


    /**
     * 设置url的query
     *
     * @param querys query的map集合
     * @return ReqBuilder {@link ReqSpec}
     */
    public T query(Map<String, Object> querys) {

        ofNullable(querys).ifPresent(q -> q.forEach((k, v) -> urlBuilder.addQueryParameter(k, String.valueOf(v))));

        return (T) this;
    }

    /**
     * 设置url的query并编码
     *
     * @param querys query的map集合
     * @return ReqBuilder {@link ReqSpec}
     */
    public T encodedQuery(Map<String, Object> querys) {

        ofNullable(querys).ifPresent(q -> q.forEach((k, v) -> urlBuilder.addEncodedQueryParameter(k, String.valueOf(v))));

        return (T) this;
    }


    /**
     * 设置url的fragment
     *
     * @param fragment #号后面的内容
     * @return ReqBuilder {@link ReqSpec}
     */
    public T fragment(String fragment) {

        ofNullable(fragment).ifPresent(urlBuilder::fragment);

        return (T) this;
    }

    /**
     * 设置url的fragment并编码
     *
     * @param fragment #号后面的内容
     * @return ReqBuilder {@link ReqSpec}
     */
    public T encodedFragment(String fragment) {

        ofNullable(fragment).ifPresent(urlBuilder::encodedFragment);

        return (T) this;
    }


    /**
     * 添加请求头，会覆盖
     *
     * @param headers map集合
     * @return ReqBuilder {@link ReqSpec}
     */
    public T headers(Map<String, String> headers) {
        notNull(headers, "headers must not be null");

        headers.forEach(builder::header);
        return (T) this;
    }

    /**
     * 添加请求头，会覆盖
     *
     * @param name  名称
     * @param value 值
     * @return ReqBuilder {@link ReqSpec}
     */
    public T header(String name, String value) {

        if (nonNull(name) && nonNull(value)) {
            builder.header(name, value);
        }

        return (T) this;
    }

    /**
     * 添加请求头，不会覆盖
     *
     * @param name  名称
     * @param value 值
     * @return ReqBuilder {@link ReqSpec}
     */
    public T addHeader(String name, String value) {

        if (nonNull(name) && nonNull(value)) {
            builder.addHeader(name, value);
        }

        return (T) this;
    }

    /**
     * 添加请求头，不会覆盖
     *
     * @param headers map集合
     * @return ReqBuilder {@link ReqSpec}
     */
    public T addHeaders(Map<String, String> headers) {
        notNull(headers, "headers must not be null");

        headers.forEach(builder::addHeader);
        return (T) this;
    }

    /**
     * 移除header
     *
     * @param name 昵称
     * @return ReqBuilder {@link ReqSpec}
     */
    public T removeHeader(String name) {

        ofNullable(name).ifPresent(builder::removeHeader);

        return (T) this;
    }

    /**
     * 添加cookie
     *
     * @param cookies map集合
     * @return ReqBuilder {@link ReqSpec}
     */
    public T cookies(Map<String, String> cookies) {

        ofNullable(cookies).ifPresent(cookieMap::putAll);

        return (T) this;
    }

    /**
     * 添加cookie
     *
     * @param k key
     * @param v value
     * @return ReqBuilder {@link ReqSpec}
     */
    public T cookie(String k, String v) {

        if (nonNull(k) && nonNull(v)) {
            cookieMap.put(k, v);
        }

        return (T) this;
    }

    /**
     * 移除添加过的cookie
     *
     * @param k cookie的值
     * @return ReqBuilder {@link ReqSpec}
     */
    public T removeCookie(String k) {

        ofNullable(k).ifPresent(cookieMap::remove);

        return (T) this;
    }

    /**
     * 设置contentType
     *
     * @param contentType {@link ContentType}
     * @return ReqBuilder {@link ReqSpec}
     */
    public T contentType(String contentType) {
        this.contentType = contentType;
        return (T) this;
    }

    /**
     * 设置charset
     *
     * @param charset 编码类型
     * @return ReqBuilder {@link ReqSpec}
     */
    public T charset(Charset charset) {
        this.charset = charset;
        return (T) this;
    }

    /**
     * 设置user-agent，可以使用{@link UserAgent}内常用的ua
     *
     * @param ua user-agent
     * @return ReqBuilder {@link ReqSpec}
     */
    public T userAgent(String ua) {
        notNull(ua, "user-agent must not be null");

        builder.header(Header.USER_AGENT.value(), ua);
        return (T) this;
    }

    /**
     * 设置authorization
     *
     * @param auth 认证凭证
     * @return ReqBuilder {@link ReqSpec}
     */
    public T auth(String auth) {
        notNull(auth, "auth must not be null");

        builder.header(Header.AUTHORIZATION.value(), auth);
        return (T) this;
    }

    /**
     * 设置bearer类型的authorization
     *
     * @param token bearer token
     * @return ReqBuilder {@link ReqSpec}
     */
    public T bearer(String token) {
        notNull(token, "token must not be null");

        return auth("Bearer " + token);
    }

    /**
     * 设置basic类型的authorization
     *
     * @param username 用户名
     * @param password 密码
     * @return ReqBuilder {@link ReqSpec}
     */
    public T basic(String username, String password) {
        notNull(username, "username must not be null");
        notNull(password, "password must not be null");

        return auth(Credentials.basic(username, password, charset()));
    }


    /**
     * 添加json字符串的body
     *
     * @param json json字符串
     * @return ReqBuilder {@link ReqSpec}
     */
    public T json(String json) {
        return body(json, ContentType.JSON.value());
    }

    /**
     * 添加数据类型的对象，使用Jackson转换成json字符串
     *
     * @param json 数据类型的对象
     * @return ReqBuilder {@link ReqSpec}
     */
    public T json(Object json) {
        return body(Json.toStr(json), ContentType.JSON.value());
    }

    /**
     * 自定义设置json对象
     *
     * @param body        内容
     * @param contentType 类型 {@link ContentType}
     * @return ReqBuilder {@link ReqSpec}
     */
    public T body(String body, String contentType) {
        return body(body.getBytes(charset()), contentType);
    }

    /**
     * 自定义设置json对象
     *
     * @param body        内容
     * @param contentType 类型 {@link ContentType}
     * @return ReqBuilder {@link ReqSpec}
     */
    public T body(byte[] body, String contentType) {
        contentType(contentType);
        this.body = body;
        return (T) this;
    }


    //get

    /**
     * 获取请求类型。
     *
     * @return 请求类型 {@link ReqType}
     */
    public ReqType reqType() {
        return reqType;
    }

    /**
     * 获取当前请求配置。
     *
     * @return 请求配置 {@link Conf}
     */
    public Conf conf() {
        return conf;
    }

    /**
     * 获取底层okhttp请求构建器。
     *
     * @return okhttp请求构建器 {@link Builder}
     */
    public Builder builder() {
        return builder;
    }

    /**
     * 获取请求方法。
     *
     * @return 请求方法 {@link Method}
     */
    public Method method() {
        return method;
    }

    /**
     * 获取当前维护的cookie集合。
     *
     * @return cookie集合
     */
    public Map<String, String> cookies() {
        return cookieMap;
    }

    /**
     * 获取当前维护的cookie集合。
     *
     * @return cookie集合
     */
    public Map<String, String> cookieMap() {
        return cookieMap;
    }

    /**
     * 获取请求内容类型。
     *
     * @return 内容类型
     */
    public String contentType() {
        return contentType;
    }

    /**
     * 获取请求字符集。
     *
     * @return 字符集
     */
    public Charset charset() {
        return charset;
    }

    /**
     * 获取url构建器。
     *
     * @return url构建器 {@link HttpUrl.Builder}
     */
    public HttpUrl.Builder urlBuilder() {
        return urlBuilder;
    }

    /**
     * 获取请求体字节数组。
     *
     * @return 请求体
     */
    public byte[] body() {
        return body;
    }

    /**
     * 获取请求附件集合。
     *
     * @return 附件集合
     */
    public Map<Object, Object> attachment() {
        return attachment;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
