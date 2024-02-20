package io.github.kongweiguang.http.client;


import io.github.kongweiguang.http.client.core.Client;
import io.github.kongweiguang.http.client.core.Conf;
import io.github.kongweiguang.http.client.core.Const;
import io.github.kongweiguang.http.client.core.ContentType;
import io.github.kongweiguang.http.client.core.Header;
import io.github.kongweiguang.http.client.core.InnerUtil;
import io.github.kongweiguang.http.client.core.Method;
import io.github.kongweiguang.http.client.core.ReqLog;
import io.github.kongweiguang.http.client.core.ReqTypeEnum;
import io.github.kongweiguang.http.client.core.Timeout;
import io.github.kongweiguang.http.client.core.UA;
import io.github.kongweiguang.http.client.sse.SSEListener;
import io.github.kongweiguang.http.client.ws.WSListener;
import io.github.kongweiguang.json.Json;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.isTure;
import static io.github.kongweiguang.core.lang.Assert.notNull;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static okhttp3.internal.http.HttpMethod.permitsRequestBody;

/**
 * http请求构建器
 *
 * @author kongweiguang
 */
public final class ReqBuilder {

    private ReqTypeEnum reqType;
    private final Conf conf;
    private final Request.Builder builder;

    //header
    private Method method;
    private Map<String, String> cookieMap;
    private String contentType;
    private Charset charset;

    //url
    private HttpUrl.Builder urlBuilder;

    //body
    private byte[] body;

    //form
    private Map<String, String> formMap;
    private MultipartBody.Builder mul;

    //async
    private Consumer<Res> success;
    private Consumer<Throwable> fail;

    //retry
    private int max;
    private Duration delay;
    private BiPredicate<Res, Throwable> predicate;

    //listener
    private WSListener wsListener;
    private SSEListener sseListener;

    //attachment
    private Map<Object, Object> attachment;

    ReqBuilder() {
        this.charset = StandardCharsets.UTF_8;
        this.method = Method.GET;
        this.reqType = ReqTypeEnum.http;
        this.builder = new Builder();
        this.conf = Conf.of();
    }

    /**
     * 配置请求
     *
     * @param conf 配置 {@link Conf}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder config(final Consumer<Conf> conf) {
        notNull(conf, "conf consumer must not be null");

        conf.accept(this.conf);
        return this;
    }

    /**
     * 禁用ssl校验
     *
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder disableSslValid() {
        conf.ssl(false);
        return this;
    }

    /**
     * 禁用重定向
     *
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder disableRedirect() {
        conf.followRedirects(false);
        return this;
    }

    /**
     * 禁用ssl重定向
     *
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder disableSslRedirect() {
        conf.followSslRedirects(false);
        return this;
    }

    /**
     * 请求的类型
     *
     * @param reqType 请求类型 {@link ReqTypeEnum}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder reqType(final ReqTypeEnum reqType) {
        this.reqType = reqType;
        return this;
    }

    /**
     * 设置请求为form_urlencoded类型
     *
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder formUrlencoded() {
        return method(Method.POST).contentType(ContentType.form_urlencoded);
    }

    /**
     * 设置请求为multipart类型
     *
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder multipart() {
        return method(Method.POST).contentType(ContentType.multipart);
    }

    /**
     * 同步请求
     *
     * @return Res {@link Res}
     */
    public Res ok() {
        return ok(Client.of(conf));
    }

    /**
     * 同步请求，自定义client
     *
     * @param client {@link OkHttpClient}
     * @return Res {@link Res}
     */
    public Res ok(final OkHttpClient client) {
        before();
        return OK.ok(this, client);
    }

    /**
     * 异步请求
     *
     * @return Res {@link ReqBuilder}
     */
    public CompletableFuture<Res> okAsync() {
        return okAsync(Client.of(conf));
    }

    /**
     * 异步请求，自定义client
     *
     * @param client {@link OkHttpClient}
     * @return Res {@link ReqBuilder}
     */
    public CompletableFuture<Res> okAsync(final OkHttpClient client) {
        before();
        return OK.okAsync(this, client);
    }

    /**
     * 请求前初始化
     */
    private void before() {
        //method url
        builder().method(method().name(), addBody()).url(urlBuilder().build());

        //cookie
        if (nonNull(cookieMap)) {
            header(Header.cookie.v(), InnerUtil.cookie2Str(cookie()));
        }

        //tag
        builder().tag(ReqBuilder.class, this);
    }


    /**
     * 添加body
     *
     * @return RequestBody {@link RequestBody}
     */
    private RequestBody addBody() {
        RequestBody rb = null;

        if (permitsRequestBody(method().name())) {
            //multipart 格式提交
            if (isMul()) {

                ofNullable(formMap).ifPresent(ignore -> form().forEach(mul()::addFormDataPart));

                rb = mul().setType(requireNonNull(MediaType.parse(contentType()))).build();

            }
            //form_urlencoded 格式提交
            else if (isFormUrl()) {

                final FormBody.Builder formBuilder = new FormBody.Builder(charset());

                ofNullable(formMap).ifPresent(ignore -> form().forEach(formBuilder::addEncoded));

                rb = formBuilder.build();

            }
            //字符串提交
            else {

                if (nonNull(body())) {
                    rb = RequestBody.create(MediaType.parse(contentType()), body());
                }

            }
        }

        return rb;
    }


    /**
     * 请求超时时间设置
     *
     * @param timeout 超时时间
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder timeout(final Duration timeout) {
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
    public ReqBuilder timeout(final Duration connect, final Duration write, final Duration read) {
        notNull(connect, "connect must not be null");
        notNull(write, "write must not be null");
        notNull(read, "read must not be null");

        conf.timeout(new Timeout(connect, write, read));

        return this;
    }

    /**
     * 设置method
     *
     * @param method {@link Method}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder method(final Method method) {
        notNull(method, "method must not be null");

        this.method = method;
        return this;
    }

    /**
     * 添加请求头，会覆盖
     *
     * @param headers map集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder headers(final Map<String, String> headers) {

        ofNullable(headers).ifPresent(h -> h.forEach(builder()::header));

        return this;
    }

    /**
     * 添加请求头，会覆盖
     *
     * @param name  名称
     * @param value 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder header(final String name, final String value) {

        if (nonNull(name) && nonNull(value)) {
            builder().header(name, value);
        }

        return this;
    }

    /**
     * 添加请求头，不会覆盖
     *
     * @param name  名称
     * @param value 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder addHeader(final String name, final String value) {

        if (nonNull(name) && nonNull(value)) {
            builder().addHeader(name, value);
        }

        return this;
    }

    /**
     * 移除header
     *
     * @param name 昵称
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder removeHeader(final String name) {

        ofNullable(name).ifPresent(builder()::removeHeader);

        return this;
    }

    /**
     * 添加cookie
     *
     * @param cookies map集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder cookies(final Map<String, String> cookies) {

        ofNullable(cookies).ifPresent(cookie()::putAll);

        return this;
    }

    /**
     * 添加cookie
     *
     * @param k key
     * @param v value
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder cookie(final String k, final String v) {

        if (nonNull(k) && nonNull(v)) {
            cookie().put(k, v);
        }

        return this;
    }

    /**
     * 移除添加过的cookie
     *
     * @param k cookie的值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder removeCookie(final String k) {

        ofNullable(k).ifPresent(cookie()::remove);

        return this;
    }

    /**
     * 设置contentType
     *
     * @param contentType {@link ContentType}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder contentType(final ContentType contentType) {

        if (nonNull(contentType)) {
            this.contentType = contentType.v();

            header(Header.content_type.v(), contentType() + ";charset=" + charset().name());
        }

        return this;
    }

    /**
     * 设置charset
     *
     * @param charset 编码类型
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder charset(final Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 设置user-agent，可以使用{@link UA}内常用的ua
     *
     * @param ua user-agent
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder ua(final String ua) {
        notNull(ua, "user-agent must not be null");

        builder().header(Header.user_agent.v(), ua);
        return this;
    }

    /**
     * 设置authorization
     *
     * @param auth 认证凭证
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder auth(final String auth) {
        notNull(auth, "auth must not be null");

        builder().header(Header.authorization.v(), auth);
        return this;
    }

    /**
     * 设置bearer类型的authorization
     *
     * @param token bearer token
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder bearer(final String token) {
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
    public ReqBuilder basic(final String username, final String password) {
        notNull(username, "username must not be null");
        notNull(password, "password must not be null");

        return auth(Credentials.basic(username, password, charset()));
    }

    /**
     * 设置url，默认请求根目录 <a href="http://localhost/">http://localhost/</a>
     *
     * @param url 请求地址
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder url(final String url) {
        notNull(url, "url must not be null");

        this.urlBuilder = HttpUrl.parse(InnerUtil.fixUrl(url.trim(), ReqTypeEnum.ws.equals(reqType()))).newBuilder();

        return this;
    }

    /**
     * 设置url的协议
     *
     * @param scheme 协议
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder scheme(final String scheme) {
        notNull(scheme, "scheme must not be null");

        String s = scheme;
        switch (scheme) {
            case Const.http:
            case Const.https: {
                break;
            }
            case Const.ws: {
                s = Const.http;
                break;
            }
            case Const.wss: {
                s = Const.https;
                break;
            }
            default:
                throw new IllegalArgumentException("unexpected scheme : " + scheme);
        }

        urlBuilder().scheme(s);
        return this;
    }

    /**
     * 设置url的主机地址
     *
     * @param host 主机地址
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder host(final String host) {
        notNull(host, "host must not be null");

        urlBuilder().host(host);
        return this;
    }

    /**
     * 设置url的端口
     *
     * @param port 端口
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder port(final int port) {
        isTure(port >= 1 && port <= 65535, "port must >= 1 && port <= 65535 ");

        urlBuilder().port(port);
        return this;
    }

    /**
     * 设置url的path
     *
     * @param path 路径
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder path(final String path) {
        notNull(path, "path must not be null");

        urlBuilder().addPathSegments(InnerUtil.removeFirstSlash(path));
        return this;
    }

    /**
     * 设置url的query
     *
     * @param k 键
     * @param v 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder query(final String k, final Object v) {

        if (nonNull(k) && nonNull(v)) {
            urlBuilder().addQueryParameter(k, String.valueOf(v));
        }

        return this;
    }

    /**
     * 设置url的query并编码
     *
     * @param k 键
     * @param v 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder encodeQuery(final String k, final Object v) {

        if (nonNull(k) && nonNull(v)) {
            urlBuilder().addEncodedQueryParameter(k, String.valueOf(v));
        }

        return this;
    }

    /**
     * 设置url的query
     *
     * @param k  键
     * @param vs 值集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder query(final String k, final Iterable<Object> vs) {

        if (nonNull(k) && nonNull(vs)) {
            vs.forEach(v -> urlBuilder().addQueryParameter(k, String.valueOf(v)));
        }

        return this;
    }

    /**
     * 设置url的query并编码
     *
     * @param k  键
     * @param vs 值集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder encodedQuery(final String k, final Iterable<Object> vs) {

        if (nonNull(k) && nonNull(vs)) {
            vs.forEach(v -> urlBuilder().addEncodedQueryParameter(k, String.valueOf(v)));
        }

        return this;
    }

    /**
     * 设置url的query
     *
     * @param querys query的map集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder query(final Map<String, Object> querys) {

        ofNullable(querys).ifPresent(q -> q.forEach((k, v) -> urlBuilder().addQueryParameter(k, String.valueOf(v))));

        return this;
    }

    /**
     * 设置url的query并编码
     *
     * @param querys query的map集合
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder encodedQuery(final Map<String, Object> querys) {

        ofNullable(querys).ifPresent(q -> q.forEach((k, v) -> urlBuilder().addEncodedQueryParameter(k, String.valueOf(v))));

        return this;
    }

    /**
     * 设置url的fragment
     *
     * @param fragment #号后面的内容
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder fragment(final String fragment) {

        ofNullable(fragment).ifPresent(urlBuilder()::fragment);

        return this;
    }

    /**
     * 设置url的fragment并编码
     *
     * @param fragment #号后面的内容
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder encodedFragment(final String fragment) {

        ofNullable(fragment).ifPresent(urlBuilder()::encodedFragment);

        return this;
    }

    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name     名称
     * @param fileName 文件名
     * @param bytes    文件内容
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder file(final String name, final String fileName, final byte[] bytes) {
        notNull(name, "name must not be null");
        notNull(fileName, "fileName must not be null");
        notNull(bytes, "bytes must not be null");

        if (isMul()) {
            mul().addFormDataPart(
                    name,
                    fileName,
                    RequestBody.create(MediaType.parse(contentType()), bytes)
            );
        } else {
            throw new IllegalArgumentException("use file must is multipart ");
        }

        return this;
    }

    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name     名称
     * @param fileName 文件名
     * @param path     文件路径
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder file(final String name, final String fileName, final Path path) {
        try {
            return file(name, fileName, Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name     名称
     * @param fileName 文件名
     * @param path     文件路径
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder file(final String name, final String fileName, final String path) {
        try {
            return file(name, fileName, Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加上传文件，只有multipart方式才可以
     *
     * @param name 名称
     * @param file 上传文件
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder file(final String name, final File file) {
        try {
            return file(name, file.getName(), Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加form表单，只有form_urlencoded或者multipart方式才可以
     *
     * @param name  名称
     * @param value 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder form(final String name, final Object value) {

        if (isFormUrl() || isMul()) {
            if (nonNull(name) && nonNull(value)) {
                form().put(name, String.valueOf(value));
            }
        } else {
            throw new IllegalArgumentException("use form table must is form_urlencoded or multipart");
        }

        return this;
    }

    /**
     * 移除表单内容根据name
     *
     * @param name 需要移除的昵称
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder removeForm(final String name) {

        if (isFormUrl() || isMul()) {
            ofNullable(name).ifPresent(form()::remove);
        } else {
            throw new IllegalArgumentException("use form table must is form_urlencoded or multipart");
        }

        return this;
    }

    /**
     * 添加form表单，只有form_urlencoded或者multipart方式才可以
     *
     * @param form form表单map
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder form(final Map<String, Object> form) {

        if (isFormUrl() || isMul()) {
            ofNullable(form).ifPresent(map -> form.forEach((k, v) -> form().put(k, String.valueOf(v))));
        } else {
            throw new IllegalArgumentException("use form table must is form_urlencoded or multipart");
        }

        return this;
    }

    /**
     * 添加json字符串的body
     *
     * @param json json字符串
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder json(final String json) {
        return body(json, ContentType.json);
    }

    /**
     * 添加数据类型的对象，使用fastjson转换成json字符串
     *
     * @param json 数据类型的对象
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder json(final Object json) {
        return body(Json.toStr(json), ContentType.json);
    }

    /**
     * 自定义设置json对象
     *
     * @param body        内容
     * @param contentType 类型 {@link ContentType}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder body(final String body, final ContentType contentType) {
        return body(body.getBytes(charset()), contentType);
    }

    /**
     * 自定义设置json对象
     *
     * @param body        内容
     * @param contentType 类型 {@link ContentType}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder body(final byte[] body, final ContentType contentType) {
        contentType(contentType);
        this.body = body;
        return this;
    }

    /**
     * 异步请求时成功时调用函数
     *
     * @param success 成功回调函数
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder success(final Consumer<Res> success) {
        notNull(success, "success must not be null");

        this.success = success;
        return this;
    }

    /**
     * 异步请求失败时调用函数
     *
     * @param fail 失败回调函数
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder fail(final Consumer<Throwable> fail) {
        notNull(fail, "fail must not be null");

        this.fail = fail;
        return this;
    }

    /**
     * 重试   设置成3会额外多请求3次，加上本身请求的一次，一共是4次
     *
     * @param max 最大重试次数
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder retry(final int max) {
        return retry(max, Duration.ofSeconds(1), (r, e) -> {
            if (nonNull(e)) {
                return true;
            }

            if (nonNull(r)) {
                return !r.isOk();
            }

            return true;
        });
    }

    /**
     * 重试   设置成3会额外多请求3次，加上本身请求的一次，一共是4次
     *
     * @param max       最大重试次数
     * @param delay     重试间隔时间
     * @param predicate 重试条件
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder retry(final int max, final Duration delay, final BiPredicate<Res, Throwable> predicate) {
        isTure(max > 0, "max must > 0");
        notNull(delay, "delay must not be null");
        notNull(predicate, "predicate must not be null");

        this.max = max;
        this.delay = delay;
        this.predicate = predicate;
        return this;
    }

    /**
     * 设置ws协议的监听函数
     *
     * @param wsListener 监听函数
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder wsListener(final WSListener wsListener) {
        notNull(wsListener, "wsListener must not be null");

        this.wsListener = wsListener;
        return this;
    }

    /**
     * sse协议调用时的监听函数
     *
     * @param sseListener 监听函数
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder sseListener(final SSEListener sseListener) {
        notNull(sseListener, "sseListener must not be null");

        this.sseListener = sseListener;
        return this;
    }

    /**
     * 请求中添加的附件
     *
     * @param k 键
     * @param v 值
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder attr(final Object k, final Object v) {
        if (isNull(attachment)) {
            this.attachment = new HashMap<>();
        }

        attachment.put(k, v);
        return this;
    }

    /**
     * 设置slf4j为日志器
     *
     * @param level 日志级别 {@link okhttp3.logging.HttpLoggingInterceptor.Level}
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder slf4j(HttpLoggingInterceptor.Level level) {
        return log(ReqLog.slf4j, level);
    }

    /**
     * 设置日志器和日志级别
     *
     * @param logger 日志器
     * @param level  日志级别
     * @return ReqBuilder {@link ReqBuilder}
     */
    public ReqBuilder log(ReqLog logger, HttpLoggingInterceptor.Level level) {
        notNull(logger, "logger must not be null");
        notNull(level, "level must not be null");

        conf.addInterceptor(InnerUtil.httpLoggingInterceptor(logger, level));
        return this;
    }


    //get
    @SuppressWarnings("unchecked")
    public <T> T attr(final Object k) {
        if (isNull(attachment)) {
            return null;
        }

        return (T) attachment.get(k);
    }

    public Builder builder() {
        return builder;
    }

    public ReqTypeEnum reqType() {
        return reqType;
    }

    public Method method() {
        return method;
    }

    public HttpUrl.Builder urlBuilder() {
        if (isNull(urlBuilder)) {
            url("");
        }

        return urlBuilder;
    }

    public byte[] body() {
        return body;
    }

    public String contentType() {
        return contentType;
    }

    public Charset charset() {
        return charset;
    }

    public Map<String, String> form() {
        if (isNull(formMap)) {
            this.formMap = new HashMap<>();
        }

        return formMap;
    }

    public Map<String, String> cookie() {
        if (isNull(cookieMap)) {
            this.cookieMap = new HashMap<>();
        }

        return cookieMap;
    }

    public MultipartBody.Builder mul() {
        if (isNull(mul)) {
            this.mul = new MultipartBody.Builder();
        }

        return mul;
    }

    public int max() {
        return max;
    }

    public Duration delay() {
        return delay;
    }

    public Consumer<Res> success() {
        return success;
    }

    public Consumer<Throwable> fail() {
        return fail;
    }

    public BiPredicate<Res, Throwable> predicate() {
        return predicate;
    }

    public WSListener wsListener() {
        return wsListener;
    }

    public SSEListener sseListener() {
        return sseListener;
    }

    public boolean isMul() {
        if (nonNull(contentType())) {
            return contentType().contains(ContentType.multipart.v());
        }

        return false;
    }

    public boolean isFormUrl() {
        if (nonNull(contentType())) {
            return contentType().contains(ContentType.form_urlencoded.v());
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
