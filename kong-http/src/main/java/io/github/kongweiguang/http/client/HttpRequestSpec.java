package io.github.kongweiguang.http.client;

import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.core.retry.RetryableTask;
import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Header;
import io.github.kongweiguang.http.client.consts.Method;
import io.github.kongweiguang.http.client.core.*;
import io.github.kongweiguang.http.client.entity.FilePart;
import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;
import io.github.kongweiguang.http.client.executor.HttpExecutor;
import io.github.kongweiguang.http.client.executor.SSEExecutor;
import io.github.kongweiguang.http.client.executor.WSExecutor;
import io.github.kongweiguang.http.client.retry.HttpRetryPolicy;
import io.github.kongweiguang.http.client.retry.NoRetryPolicy;
import io.github.kongweiguang.http.client.sse.SSEListener;
import io.github.kongweiguang.http.client.ws.WSListener;
import io.github.kongweiguang.json.Json;
import okhttp3.HttpUrl;
import okhttp3.logging.HttpLoggingInterceptor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.http.client.utils.HttpClientUtil.fixUrl;
import static java.util.Objects.nonNull;

/**
 * kong-http 的请求规范与链式构建对象。
 *
 * @author kongweiguang
 */
public final class HttpRequestSpec {

    /**
     * 保存 request type
     */
    private ReqType reqType = ReqType.http;


    /**
     * 保存 HTTP method
     */
    private Method method = Method.GET;


    /**
     * 保存规范化后的 request URL builder
     */
    private HttpUrl.Builder url = parseUrlBuilder("http://localhost");


    /**
     * 保存 request headers
     */
    private final Map<String, String> headers = new LinkedHashMap<>();


    /**
     * 保存 request cookies
     */
    private final Map<String, String> cookies = new LinkedHashMap<>();


    /**
     * 保存未编码的 query parameters
     */
    private final Map<String, Object> query = new LinkedHashMap<>();


    /**
     * 保存已编码的 query parameters
     */
    private final Map<String, Object> encodedQuery = new LinkedHashMap<>();


    /**
     * 保存未编码的 URL fragment
     */
    private String fragment;


    /**
     * 保存已编码的 URL fragment
     */
    private String encodedFragment;


    /**
     * 保存 request content type
     */
    private String contentType;


    /**
     * 保存 request content 使用的 charset
     */
    private Charset charset = StandardCharsets.UTF_8;


    /**
     * 保存 request body bytes
     */
    private byte[] body;


    /**
     * 保存 form fields
     */
    private final Map<String, String> form = new LinkedHashMap<>();


    /**
     * 保存 uploaded file parts
     */
    private final List<FilePart> files = new ArrayList<>();


    /**
     * 保存自定义 attrs
     */
    private final Map<Object, Object> attrs = new HashMap<>();


    /**
     * 保存 request config
     */
    private final Conf conf = Conf.global().copy();


    /**
     * 保存 success callback
     */
    private Consumer<Res> onSuccess;


    /**
     * 保存 failure callback
     */
    private Consumer<Throwable> onFailure;


    /**
     * 保存 retry config
     */
    private final RetryableTask<Res> retry = RetryableTask.<Res>retryForPredicate(() -> null, (res, t) -> {
                if (t != null) {
                    return Pair.of(true, res);
                }
                if (res != null) {
                    return Pair.of(!res.isOk(), res);
                }
                return Pair.of(false, res);
            })
            .maxAttempts(1);


    /**
     * 保存 SSE listener
     */
    private SSEListener sseListener;


    /**
     * 保存 WebSocket listener
     */
    private WSListener wsListener;


    /**
     * 仅包内设置 req type
     */
    HttpRequestSpec reqType(ReqType reqType) {
        this.reqType = reqType;
        return this;
    }


    /**
     * 返回 req type
     */
    public ReqType reqType() {
        return reqType;
    }


    /**
     * 返回 method
     */
    public Method method() {
        return method;
    }


    /**
     * 返回 url
     */
    public String url() {
        return url.build().toString();
    }


    /**
     * 返回当前 URL builder 的副本，供发送前继续组装。
     */
    public HttpUrl.Builder urlBuilder() {
        return url.build().newBuilder();
    }


    /**
     * 返回 headers
     */
    public Map<String, String> headers() {
        return headers;
    }


    /**
     * 返回 cookies
     */
    public Map<String, String> cookies() {
        return cookies;
    }


    /**
     * 返回 query
     */
    public Map<String, Object> query() {
        return query;
    }


    /**
     * 返回 encoded query
     */
    public Map<String, Object> encodedQuery() {
        return encodedQuery;
    }


    /**
     * 返回 fragment
     */
    public String fragment() {
        return fragment;
    }


    /**
     * 返回 encoded fragment
     */
    public String encodedFragment() {
        return encodedFragment;
    }


    /**
     * 返回 content type
     */
    public String contentType() {
        return contentType;
    }


    /**
     * 返回 charset
     */
    public Charset charset() {
        return charset;
    }


    /**
     * 返回 body content
     */
    public byte[] body() {
        return body == null ? null : Arrays.copyOf(body, body.length);
    }


    /**
     * 返回 form
     */
    public Map<String, String> form() {
        return form;
    }


    /**
     * 返回 files
     */
    public List<FilePart> files() {
        return files;
    }


    /**
     * 返回 conf
     */
    public Conf conf() {
        return conf;
    }


    /**
     * 返回 on success
     */
    public Consumer<Res> onSuccess() {
        return onSuccess;
    }


    /**
     * 返回 on failure
     */
    public Consumer<Throwable> onFailure() {
        return onFailure;
    }


    /**
     * 配置 retry behavior
     */
    public RetryableTask<Res> retry() {
        return retry;
    }


    /**
     * 返回 sse listener
     */
    public SSEListener sseListener() {
        return sseListener;
    }


    /**
     * 返回 ws listener
     */
    public WSListener wsListener() {
        return wsListener;
    }


    /**
     * 读取自定义 attr
     */
    @SuppressWarnings("unchecked")
    public <T> T attr(Object key) {
        return (T) attrs.get(key);
    }


    /**
     * 设置 method
     */
    public HttpRequestSpec method(Method method) {
        this.method = method;
        return this;
    }


    /**
     * 设置 url
     */
    public HttpRequestSpec url(String url) {
        notNull(url, "url must not be null");
        this.url = parseUrlBuilder(url);
        return this;
    }


    /**
     * 根据 name 返回 header value
     */
    public HttpRequestSpec header(String name, String value) {
        if (nonNull(name) && nonNull(value)) {
            headers.put(name, value);
        }
        return this;
    }


    /**
     * 添加 add header 数据
     */
    public HttpRequestSpec addHeader(String name, String value) {
        if (nonNull(name) && nonNull(value)) {
            headers.merge(name, value, (o, n) -> n);
        }
        return this;
    }


    /**
     * 设置 headers
     */
    public HttpRequestSpec headers(Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(this::header);
        }
        return this;
    }


    /**
     * 设置 cookie
     */
    public HttpRequestSpec cookie(String key, String value) {
        if (nonNull(key) && nonNull(value)) {
            cookies.put(key, value);
        }
        return this;
    }


    /**
     * 设置 cookies
     */
    public HttpRequestSpec cookies(Map<String, String> cookies) {
        if (cookies != null) {
            this.cookies.putAll(cookies);
        }
        return this;
    }


    /**
     * 设置 query
     */
    public HttpRequestSpec query(String key, Object value) {
        if (nonNull(key) && nonNull(value)) {
            query.put(key, value);
        }
        return this;
    }

    /**
     * 批量设置 query
     */
    public HttpRequestSpec query(Map<String, Object> queryMap) {
        if (queryMap != null) {
            queryMap.forEach(this::query);
        }
        return this;
    }


    /**
     * 设置 encoded query
     */
    public HttpRequestSpec encodedQuery(String key, Object value) {
        if (nonNull(key) && nonNull(value)) {
            encodedQuery.put(key, value);
        }
        return this;
    }


    /**
     * 设置 fragment
     */
    public HttpRequestSpec fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }


    /**
     * 设置 encoded fragment
     */
    public HttpRequestSpec encodedFragment(String fragment) {
        this.encodedFragment = fragment;
        return this;
    }


    /**
     * 设置 content type
     */
    public HttpRequestSpec contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }


    /**
     * 设置 charset
     */
    public HttpRequestSpec charset(Charset charset) {
        this.charset = charset;
        return this;
    }


    /**
     * 设置 body content
     */
    public HttpRequestSpec body(byte[] body) {
        this.body = body == null ? null : Arrays.copyOf(body, body.length);
        return this;
    }


    /**
     * 设置 body content
     */
    public HttpRequestSpec body(String body) {
        this.body = body == null ? null : body.getBytes(charset);
        return this;
    }


    /**
     * 设置 body content
     */
    public HttpRequestSpec body(String body, String contentType) {
        return body(body).contentType(contentType);
    }


    /**
     * 设置 form
     */
    public HttpRequestSpec form(String key, Object value) {
        if (nonNull(key) && nonNull(value)) {
            form.put(key, String.valueOf(value));
        }
        return this;
    }


    /**
     * 设置 form
     */
    public HttpRequestSpec form(Map<String, Object> formMap) {
        if (formMap != null) {
            formMap.forEach(this::form);
        }
        return this;
    }


    /**
     * 将 content 写入指定 file
     */
    public HttpRequestSpec file(String name, String fileName, byte[] bytes) {
        notNull(name, "name must not be null");
        notNull(fileName, "fileName must not be null");
        notNull(bytes, "bytes must not be null");
        files.add(new FilePart(name, fileName, Arrays.copyOf(bytes, bytes.length)));
        return this;
    }

    /**
     * 将本地文件写入 multipart file
     */
    public HttpRequestSpec file(String name, String fileName, String path) {
        notNull(path, "path must not be null");
        try {
            return file(name, fileName, Files.readAllBytes(Path.of(path)));
        } catch (Exception e) {
            throw new KongHttpRuntimeException(e);
        }
    }


    /**
     * 设置自定义 attr
     */
    public HttpRequestSpec attr(Object key, Object value) {
        attrs.put(key, value);
        return this;
    }


    /**
     * 应用 request config 修改
     */
    public HttpRequestSpec config(Consumer<Conf> confConsumer) {
        notNull(confConsumer, "conf consumer must not be null");
        confConsumer.accept(conf);
        return this;
    }


    /**
     * 设置 success callback
     */
    public HttpRequestSpec success(Consumer<Res> success) {
        this.onSuccess = success;
        return this;
    }


    /**
     * 设置 failure callback
     */
    public HttpRequestSpec fail(Consumer<Throwable> fail) {
        this.onFailure = fail;
        return this;
    }


    /**
     * 配置 retry behavior
     */
    public HttpRequestSpec retry(Consumer<RetryableTask<Res>> retryConsumer) {
        notNull(retryConsumer, "retry consumer must not be null");
        retryConsumer.accept(retry);
        return this;
    }


    /**
     * 设置 sse listener
     */
    public HttpRequestSpec sseListener(SSEListener listener) {
        this.sseListener = listener;
        return this;
    }


    /**
     * 设置 ws listener
     */
    public HttpRequestSpec wsListener(WSListener listener) {
        this.wsListener = listener;
        return this;
    }

    /**
     * 设置基础认证头
     */
    public HttpRequestSpec auth(String authorization) {
        return header(Header.AUTHORIZATION.v(), authorization);
    }

    /**
     * 设置 bearer token
     */
    public HttpRequestSpec bearer(String token) {
        return token == null ? this : header(Header.AUTHORIZATION.v(), "Bearer " + token);
    }

    /**
     * 设置 user agent
     */
    public HttpRequestSpec userAgent(String userAgent) {
        return header(Header.USER_AGENT.v(), userAgent);
    }

    /**
     * 对象转 JSON body
     */
    public HttpRequestSpec json(Object obj) {
        return body(Json.toStr(obj), ContentType.JSON.v());
    }

    /**
     * 设置统一超时
     */
    public HttpRequestSpec timeout(Duration timeout) {
        return timeout(timeout, timeout, timeout);
    }

    /**
     * 设置连接/写入/读取超时
     */
    public HttpRequestSpec timeout(Duration connect, Duration write, Duration read) {
        return config(c -> c.timeout(new Timeout(connect, write, read)));
    }

    /**
     * 设置日志打印
     */
    public HttpRequestSpec log(ReqLog logger, HttpLoggingInterceptor.Level level) {
        return config(c -> c.log(logger, level));
    }

    /**
     * 覆盖 URL scheme
     */
    public HttpRequestSpec scheme(String scheme) {
        if (scheme == null) {
            return this;
        }
        this.url.scheme(scheme);
        return this;
    }

    /**
     * 覆盖 URL host
     */
    public HttpRequestSpec host(String host) {
        if (host == null) {
            return this;
        }
        this.url.host(host);
        return this;
    }

    /**
     * 覆盖 URL port
     */
    public HttpRequestSpec port(int port) {
        this.url.port(port);
        return this;
    }

    /**
     * 追加 URL path segment
     */
    public HttpRequestSpec path(String path) {
        if (path == null) {
            return this;
        }
        for (String segment : path.split("/")) {
            if (!segment.isEmpty()) {
                this.url.addPathSegment(segment);
            }
        }
        return this;
    }


    /**
     * 构建并同步执行请求
     */
    @SuppressWarnings("unchecked")
    public <T> T ok() {
        return (T) executeAsync(this).join();
    }


    /**
     * 构建并异步执行请求
     */
    public <T> CompletableFuture<T> okAsync() {
        return executeAsync(this);
    }


    /**
     * 异步执行请求
     */
    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<T> executeAsync(HttpRequestSpec spec) {
        return switch (spec.reqType()) {
            case ws ->
                    (CompletableFuture<T>) new WSExecutor(spec, Client.of(spec.conf()), new NoRetryPolicy<>(), ResultHandler.noop())
                            .executeAsync();
            case sse ->
                    (CompletableFuture<T>) new SSEExecutor(spec, Client.of(spec.conf()), new NoRetryPolicy<>(), ResultHandler.noop())
                            .executeAsync();
            case http ->
                    (CompletableFuture<T>) new HttpExecutor(spec, Client.of(spec.conf()), new HttpRetryPolicy(spec.retry()), toHandler(spec))
                            .executeAsync();
        };
    }


    /**
     * 构造 HTTP 请求回调处理器
     */
    private static ResultHandler<Res> toHandler(HttpRequestSpec spec) {
        return new ResultHandler<>() {
            @Override
            public void onSuccess(Res result) {
                if (spec.onSuccess() != null) {
                    spec.onSuccess().accept(result);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                if (spec.onFailure() != null) {
                    spec.onFailure().accept(error);
                }
            }
        };
    }

    private static HttpUrl.Builder parseUrlBuilder(String url) {
        String fixed = fixUrl(url == null ? null : url.trim());
        HttpUrl parsed = HttpUrl.parse(fixed);
        if (parsed == null) {
            throw new KongHttpRuntimeException("invalid url: " + url);
        }
        return parsed.newBuilder();
    }
}
