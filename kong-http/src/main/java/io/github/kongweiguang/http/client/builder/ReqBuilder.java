package io.github.kongweiguang.http.client.builder;

import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.core.*;
import io.github.kongweiguang.http.common.core.*;
import okhttp3.*;
import okhttp3.Request.Builder;
import okhttp3.logging.HttpLoggingInterceptor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.isTrue;
import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.http.common.utils.HttpClientUtil.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static io.github.kongweiguang.core.lang.Opt.ofNullable;

/**
 * http请求构建器
 *
 * @author kongweiguang
 */
@SuppressWarnings("unchecked")
public abstract class ReqBuilder<T extends ReqBuilder<T, R>, R> {

    protected ReqType reqType;
    protected final Conf conf;
    protected final Request.Builder builder;

    //header
    protected Method method;
    protected Map<String, String> cookieMap;
    protected String contentType;
    protected Charset charset;

    //url
    protected HttpUrl.Builder urlBuilder;

    //attachment
    protected Map<Object, Object> attachment;

    protected ReqBuilder() {
        this.charset = StandardCharsets.UTF_8;
        this.method = Method.GET;
        this.reqType = ReqType.http;
        this.builder = new Builder();
        this.conf = Conf.global();
        this.urlBuilder = new HttpUrl.Builder();
    }

    /**
     * 配置请求
     *
     * @param c 配置 {@link Conf}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T config(Consumer<Conf> c) {
        notNull(c, "conf consumer must not be null");

        c.accept(conf);
        return (T) this;
    }

    /**
     * 禁用ssl校验
     *
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T disableSslValid() {
        conf.ssl(false);
        return (T) this;
    }

    /**
     * 禁用重定向
     *
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T disableRedirect() {
        conf.followRedirects(false);
        return (T) this;
    }

    /**
     * 禁用ssl重定向
     *
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T disableSslRedirect() {
        conf.followSslRedirects(false);
        return (T) this;
    }

    /**
     * 请求的类型
     *
     * @param reqType 请求类型 {@link ReqType}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T reqType(ReqType reqType) {
        this.reqType = reqType;
        return (T) this;
    }

    /**
     * 添加请求体
     *
     * @return RequestBody {@link RequestBody}
     * @see RequestBody
     * @see MultipartBody
     * @see FormBody
     **/
    protected RequestBody addBody() {
        return null;
    }

    /**
     * 请求前初始化
     */
    protected void before() {

        // 兼容okhttp的指定方法不能带body的问题，指定方法必须有请求体
        RequestBody requestBody = addBody();
        switch (method()) {
            case GET:
            case HEAD: {
                requestBody = null;
                break;
            }
            case POST:
            case PUT:
            case PATCH: {
                requestBody = ofNullable(requestBody).orElse(RequestBody.create(new byte[0]));
                break;
            }
        }

        //method url
        builder().method(method().name(), requestBody).url(urlBuilder().build());

        //cookie
        ofNullable(cookieMap).ifPresent(e -> header(Header.COOKIE.v(), cookie2Str(cookie())));

        //
        Class aClass = this.getClass();
        builder().tag(aClass, this);
    }

    /**
     * 同步请求，自定义client
     *
     * @param client {@link OkHttpClient}
     * @return Res {@link Res}
     */
    public abstract R ok(OkHttpClient client);

    /**
     * 同步请求，使用全局配置
     *
     * @return Res {@link Res}
     */
    public R ok() {
        return ok(Client.of(conf));
    }

    /**
     * 请求超时时间设置
     *
     * @param timeout 超时时间
     * @return ReqBuilder {@link ReqBuilder}
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
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T timeout(Duration connect, Duration write, Duration read) {
        notNull(connect, "connect must not be null");
        notNull(write, "write must not be null");
        notNull(read, "read must not be null");

        conf.timeout(new Timeout(connect, write, read));

        return (T) this;
    }

    /**
     * 设置method
     *
     * @param method {@link Method}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T method(Method method) {
        notNull(method, "method must not be null");

        this.method = method;
        return (T) this;
    }

    /**
     * 添加请求头，会覆盖
     *
     * @param headers map集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T headers(Map<String, String> headers) {
        notNull(headers, "headers must not be null");

        headers.forEach(builder()::header);
        return (T) this;
    }

    /**
     * 添加请求头，会覆盖
     *
     * @param name  名称
     * @param value 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T header(String name, String value) {

        if (nonNull(name) && nonNull(value)) {
            builder().header(name, value);
        }

        return (T) this;
    }

    /**
     * 添加请求头，不会覆盖
     *
     * @param name  名称
     * @param value 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T addHeader(String name, String value) {

        if (nonNull(name) && nonNull(value)) {
            builder().addHeader(name, value);
        }

        return (T) this;
    }

    /**
     * 添加请求头，不会覆盖
     *
     * @param headers map集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T addHeaders(Map<String, String> headers) {
        notNull(headers, "headers must not be null");

        headers.forEach(builder()::addHeader);
        return (T) this;
    }

    /**
     * 移除header
     *
     * @param name 昵称
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T removeHeader(String name) {

        ofNullable(name).ifPresent(builder()::removeHeader);

        return (T) this;
    }

    /**
     * 添加cookie
     *
     * @param cookies map集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T cookies(Map<String, String> cookies) {

        ofNullable(cookies).ifPresent(cookie()::putAll);

        return (T) this;
    }

    /**
     * 添加cookie
     *
     * @param k key
     * @param v value
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T cookie(String k, String v) {

        if (nonNull(k) && nonNull(v)) {
            cookie().put(k, v);
        }

        return (T) this;
    }

    /**
     * 移除添加过的cookie
     *
     * @param k cookie的值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T removeCookie(String k) {

        ofNullable(k).ifPresent(cookie()::remove);

        return (T) this;
    }

    /**
     * 设置contentType
     *
     * @param contentType {@link ContentType}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T contentType(String contentType) {

        if (nonNull(contentType)) {
            this.contentType = contentType;

            header(Header.CONTENT_TYPE.v(), contentType() + ";charset=" + charset().name());
        }

        return (T) this;
    }

    /**
     * 设置charset
     *
     * @param charset 编码类型
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T charset(Charset charset) {
        this.charset = charset;
        return (T) this;
    }

    /**
     * 设置user-agent，可以使用{@link UserAgent}内常用的ua
     *
     * @param ua user-agent
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T userAgent(String ua) {
        notNull(ua, "user-agent must not be null");

        builder().header(Header.USER_AGENT.v(), ua);
        return (T) this;
    }

    /**
     * 设置authorization
     *
     * @param auth 认证凭证
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T auth(String auth) {
        notNull(auth, "auth must not be null");

        builder().header(Header.AUTHORIZATION.v(), auth);
        return (T) this;
    }

    /**
     * 设置bearer类型的authorization
     *
     * @param token bearer token
     * @return ReqBuilder {@link ReqBuilder}
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
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T basic(String username, String password) {
        notNull(username, "username must not be null");
        notNull(password, "password must not be null");

        return auth(Credentials.basic(username, password, charset()));
    }

    /**
     * 设置url，默认请求根目录 <a href="http://localhost/">http://localhost</a>
     *
     * @param url 请求地址
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T url(String url) {
        notNull(url, "url must not be null");

        this.urlBuilder = ofNullable(HttpUrl.parse(fixUrl(url.trim()))).map(HttpUrl::newBuilder).orElse(urlBuilder());

        return (T) this;
    }

    /**
     * 设置url的协议
     *
     * @param scheme 协议
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T scheme(String scheme) {
        notNull(scheme, "scheme must not be null");

        switch (scheme) {
            case Const.http:
            case Const.https: {
                break;
            }
            case Const.ws: {
                scheme = Const.http;
                break;
            }
            case Const.wss: {
                scheme = Const.https;
                break;
            }
            default:
                throw new IllegalArgumentException("unexpected scheme : " + scheme);
        }

        urlBuilder().scheme(scheme);
        return (T) this;
    }

    /**
     * 设置url的主机地址
     *
     * @param host 主机地址
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T host(String host) {
        notNull(host, "host must not be null");

        urlBuilder().host(host);
        return (T) this;
    }

    /**
     * 设置url的端口
     *
     * @param port 端口
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T port(int port) {
        isTrue(port >= 1 && port <= 65535, "port must >= 1 && port <= 65535 ");

        urlBuilder().port(port);
        return (T) this;
    }

    /**
     * 设置url的path
     *
     * @param path 路径
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T path(String path) {
        notNull(path, "path must not be null");

        urlBuilder().addPathSegments(path);
        return (T) this;
    }

    /**
     * 设置url的query
     *
     * @param k 键
     * @param v 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T query(String k, Object v) {

        if (nonNull(k) && nonNull(v)) {
            urlBuilder().addQueryParameter(k, String.valueOf(v));
        }

        return (T) this;
    }

    /**
     * 设置url的query并编码
     *
     * @param k 键
     * @param v 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T encodeQuery(String k, Object v) {

        if (nonNull(k) && nonNull(v)) {
            urlBuilder().addEncodedQueryParameter(k, String.valueOf(v));
        }

        return (T) this;
    }

    /**
     * 设置url的query
     *
     * @param k  键
     * @param vs 值集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T query(String k, Iterable<Object> vs) {

        if (nonNull(k) && nonNull(vs)) {
            vs.forEach(v -> urlBuilder().addQueryParameter(k, String.valueOf(v)));
        }

        return (T) this;
    }

    /**
     * 设置url的query并编码
     *
     * @param k  键
     * @param vs 值集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T encodedQuery(String k, Iterable<Object> vs) {

        if (nonNull(k) && nonNull(vs)) {
            vs.forEach(v -> urlBuilder().addEncodedQueryParameter(k, String.valueOf(v)));
        }

        return (T) this;
    }

    /**
     * 设置url的query
     *
     * @param querys query的map集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T query(Map<String, Object> querys) {

        ofNullable(querys).ifPresent(q -> q.forEach((k, v) -> urlBuilder().addQueryParameter(k, String.valueOf(v))));

        return (T) this;
    }

    /**
     * 设置url的query并编码
     *
     * @param querys query的map集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T encodedQuery(Map<String, Object> querys) {

        ofNullable(querys).ifPresent(q -> q.forEach((k, v) -> urlBuilder().addEncodedQueryParameter(k, String.valueOf(v))));

        return (T) this;
    }

    /**
     * 设置url的fragment
     *
     * @param fragment #号后面的内容
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T fragment(String fragment) {

        ofNullable(fragment).ifPresent(urlBuilder()::fragment);

        return (T) this;
    }

    /**
     * 设置url的fragment并编码
     *
     * @param fragment #号后面的内容
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T encodedFragment(String fragment) {

        ofNullable(fragment).ifPresent(urlBuilder()::encodedFragment);

        return (T) this;
    }


    /**
     * 请求中添加的附件
     *
     * @param k 键
     * @param v 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T attr(Object k, Object v) {
        if (isNull(attachment)) {
            this.attachment = new HashMap<>();
        }

        attachment.put(k, v);
        return (T) this;
    }

    /**
     * 设置slf4j为日志器
     *
     * @param level 日志级别 {@link okhttp3.logging.HttpLoggingInterceptor.Level}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T slf4j(HttpLoggingInterceptor.Level level) {
        return log(ReqLog.slf4j, level);
    }

    /**
     * 设置日志器和日志级别
     *
     * @param logger 日志器
     * @param level  日志级别
     * @return ReqBuilder {@link ReqBuilder}
     */
    public T log(ReqLog logger, HttpLoggingInterceptor.Level level) {
        notNull(logger, "logger must not be null");
        notNull(level, "level must not be null");

        conf.addInterceptor(httpLoggingInterceptor(logger, level));
        return (T) this;
    }


    //get
    public <A> A attr(Object k) {
        if (isNull(attachment)) {
            return null;
        }

        return (A) attachment.get(k);
    }

    public Builder builder() {
        return builder;
    }

    public ReqType reqType() {
        return reqType;
    }

    public Method method() {
        return method;
    }

    public HttpUrl.Builder urlBuilder() {
        return urlBuilder;
    }

    public String contentType() {
        return contentType;
    }

    public Charset charset() {
        return charset;
    }


    public Map<String, String> cookie() {
        if (isNull(cookieMap)) {
            this.cookieMap = new HashMap<>();
        }

        return cookieMap;
    }

    public boolean isMul() {
        if (nonNull(contentType())) {
            return contentType().contains(ContentType.MULTIPART.v());
        }

        return false;
    }

    public boolean isFormUrl() {
        if (nonNull(contentType())) {
            return contentType().contains(ContentType.FORM_URLENCODED.v());
        }

        return false;
    }

    public Conf config() {
        return conf;
    }

    @Override
    public String toString() {
        return builder().toString();
    }
}
