package io.github.kongweiguang.http.client;

import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.core.retry.RetryableTask;
import io.github.kongweiguang.http.client.consts.Method;
import io.github.kongweiguang.http.client.core.Conf;
import io.github.kongweiguang.http.client.core.ReqType;
import io.github.kongweiguang.http.client.sse.SSEListener;
import io.github.kongweiguang.http.client.ws.WSListener;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.http.client.utils.HttpClientUtil.fixUrl;
import static java.util.Objects.nonNull;

/**
 * kong-http 的不可变请求规范
 */
public final class HttpRequestSpec {

    /**
     * 保存 request type
     */
    private final ReqType reqType;


    /**
     * 保存 HTTP method
     */
    private final Method method;


    /**
     * 保存规范化后的 request URL
     */
    private final String url;


    /**
     * 保存 request headers
     */
    private final Map<String, String> headers;


    /**
     * 保存 request cookies
     */
    private final Map<String, String> cookies;


    /**
     * 保存未编码的 query parameters
     */
    private final Map<String, Object> query;


    /**
     * 保存已编码的 query parameters
     */
    private final Map<String, Object> encodedQuery;


    /**
     * 保存未编码的 URL fragment
     */
    private final String fragment;


    /**
     * 保存已编码的 URL fragment
     */
    private final String encodedFragment;


    /**
     * 保存 request content type
     */
    private final String contentType;


    /**
     * 保存 request content 使用的 charset
     */
    private final Charset charset;


    /**
     * 保存 request body bytes
     */
    private final byte[] body;


    /**
     * 保存 form fields
     */
    private final Map<String, String> form;


    /**
     * 保存 uploaded file parts
     */
    private final List<FilePart> files;


    /**
     * 保存自定义 attrs
     */
    private final Map<Object, Object> attrs;


    /**
     * 保存 request config
     */
    private final Conf conf;


    /**
     * 保存 success callback
     */
    private final Consumer<Res> onSuccess;


    /**
     * 保存 failure callback
     */
    private final Consumer<Throwable> onFailure;


    /**
     * 保存 retry config
     */
    private final RetryableTask<Res> retry;


    /**
     * 保存 SSE listener
     */
    private final SSEListener sseListener;


    /**
     * 保存 WebSocket listener
     */
    private final WSListener wsListener;


    /**
     * 创建 HttpRequestSpec instance
     */
    private HttpRequestSpec(Builder b) {
        this.reqType = b.reqType;
        this.method = b.method;
        this.url = b.url;
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(b.headers));
        this.cookies = Collections.unmodifiableMap(new LinkedHashMap<>(b.cookies));
        this.query = Collections.unmodifiableMap(new LinkedHashMap<>(b.query));
        this.encodedQuery = Collections.unmodifiableMap(new LinkedHashMap<>(b.encodedQuery));
        this.fragment = b.fragment;
        this.encodedFragment = b.encodedFragment;
        this.contentType = b.contentType;
        this.charset = b.charset;
        this.body = b.body == null ? null : Arrays.copyOf(b.body, b.body.length);
        this.form = Collections.unmodifiableMap(new LinkedHashMap<>(b.form));
        this.files = Collections.unmodifiableList(new ArrayList<>(b.files));
        this.attrs = Collections.unmodifiableMap(new HashMap<>(b.attrs));
        this.conf = b.conf.copy();
        this.onSuccess = b.onSuccess;
        this.onFailure = b.onFailure;
        this.retry = b.retry;
        this.sseListener = b.sseListener;
        this.wsListener = b.wsListener;
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
        return url;
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
     * 返回或设置 body content
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
     * 读取或设置自定义 attr
     */
    @SuppressWarnings("unchecked")
    public <T> T attr(Object key) {
        return (T) attrs.get(key);
    }


    public static final class Builder {

        /**
         * 保存 request type
         */
        private ReqType reqType = ReqType.http;


        /**
         * 保存 HTTP method
         */
        private Method method = Method.GET;


        /**
         * 保存规范化后的 request URL
         */
        private String url = "http://localhost";


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
                    if (t != null) return Pair.of(true, res);
                    if (res != null) return Pair.of(!res.isOk(), res);
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
         * 设置 req type
         */
        public Builder reqType(ReqType reqType) {
            this.reqType = reqType;
            return this;
        }


        /**
         * 设置 method
         */
        public Builder method(Method method) {
            this.method = method;
            return this;
        }


        /**
         * 设置 url
         */
        public Builder url(String url) {
            notNull(url, "url must not be null");
            this.url = fixUrl(url.trim());
            return this;
        }


        /**
         * 根据 name 返回 header value
         */
        public Builder header(String name, String value) {
            if (nonNull(name) && nonNull(value)) headers.put(name, value);
            return this;
        }


        /**
         * 添加 add header 数据
         */
        public Builder addHeader(String name, String value) {
            if (nonNull(name) && nonNull(value)) headers.merge(name, value, (o, n) -> n);
            return this;
        }


        /**
         * 设置 headers
         */
        public Builder headers(Map<String, String> headers) {
            if (headers != null) headers.forEach(this::header);
            return this;
        }


        /**
         * 设置 cookie
         */
        public Builder cookie(String key, String value) {
            if (nonNull(key) && nonNull(value)) cookies.put(key, value);
            return this;
        }


        /**
         * 设置 cookies
         */
        public Builder cookies(Map<String, String> cookies) {
            if (cookies != null) this.cookies.putAll(cookies);
            return this;
        }


        /**
         * 设置 query
         */
        public Builder query(String key, Object value) {
            if (nonNull(key) && nonNull(value)) query.put(key, value);
            return this;
        }


        /**
         * 设置 encoded query
         */
        public Builder encodedQuery(String key, Object value) {
            if (nonNull(key) && nonNull(value)) encodedQuery.put(key, value);
            return this;
        }


        /**
         * 设置 fragment
         */
        public Builder fragment(String fragment) {
            this.fragment = fragment;
            return this;
        }


        /**
         * 设置 encoded fragment
         */
        public Builder encodedFragment(String fragment) {
            this.encodedFragment = fragment;
            return this;
        }


        /**
         * 设置 content type
         */
        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }


        /**
         * 设置 charset
         */
        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }


        /**
         * 设置 body content
         */
        public Builder body(byte[] body) {
            this.body = body == null ? null : Arrays.copyOf(body, body.length);
            return this;
        }


        /**
         * 设置 body content
         */
        public Builder body(String body) {
            this.body = body == null ? null : body.getBytes(charset);
            return this;
        }


        /**
         * 设置 body content
         */
        public Builder body(String body, String contentType) {
            return body(body).contentType(contentType);
        }


        /**
         * 设置 form
         */
        public Builder form(String key, Object value) {
            if (nonNull(key) && nonNull(value)) form.put(key, String.valueOf(value));
            return this;
        }


        /**
         * 设置 form
         */
        public Builder form(Map<String, Object> formMap) {
            if (formMap != null) formMap.forEach((k, v) -> form(k, v));
            return this;
        }


        /**
         * 将 content 写入指定 file
         */
        public Builder file(String name, String fileName, byte[] bytes) {

            notNull(name, "name must not be null");

            notNull(fileName, "fileName must not be null");

            notNull(bytes, "bytes must not be null");

            files.add(new FilePart(name, fileName, Arrays.copyOf(bytes, bytes.length)));
            return this;
        }


        /**
         * 读取或设置自定义 attr
         */
        public Builder attr(Object key, Object value) {
            attrs.put(key, value);
            return this;
        }


        /**
         * 应用 request config 修改
         */
        public Builder config(Consumer<Conf> confConsumer) {

            notNull(confConsumer, "conf consumer must not be null");

            confConsumer.accept(conf);
            return this;
        }


        /**
         * 设置 success callback
         */
        public Builder success(Consumer<Res> success) {
            this.onSuccess = success;
            return this;
        }


        /**
         * 设置 failure callback
         */
        public Builder fail(Consumer<Throwable> fail) {
            this.onFailure = fail;
            return this;
        }


        /**
         * 配置 retry behavior
         */
        public Builder retry(Consumer<RetryableTask<Res>> retryConsumer) {

            notNull(retryConsumer, "retry consumer must not be null");

            retryConsumer.accept(retry);
            return this;
        }


        /**
         * 设置 sse listener
         */
        public Builder sseListener(SSEListener listener) {
            this.sseListener = listener;
            return this;
        }


        /**
         * 设置 ws listener
         */
        public Builder wsListener(WSListener listener) {
            this.wsListener = listener;
            return this;
        }


        /**
         * 构建 HttpRequestSpec instance
         */
        public HttpRequestSpec build() {
            return new HttpRequestSpec(this);
        }
    }


    public static final class FilePart {


        /**
         * 保存 field name
         */
        private final String name;


        /**
         * 保存 uploaded file name
         */
        private final String fileName;


        /**
         * 保存 binary content
         */
        private final byte[] bytes;


        /**
         * 创建 FilePart instance
         */
        public FilePart(String name, String fileName, byte[] bytes) {
            this.name = name;
            this.fileName = fileName;
            this.bytes = bytes;
        }


        /**
         * 返回 name
         */
        public String name() {
            return name;
        }


        /**
         * 返回 file name
         */
        public String fileName() {
            return fileName;
        }


        /**
         * 以 byte array 形式返回 content
         */
        public byte[] bytes() {
            return bytes;
        }
    }
}

