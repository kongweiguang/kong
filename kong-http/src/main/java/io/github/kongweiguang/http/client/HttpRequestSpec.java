package io.github.kongweiguang.http.client;

import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.core.retry.RetryableTask;
import io.github.kongweiguang.http.client.core.Conf;
import io.github.kongweiguang.http.client.core.ReqType;
import io.github.kongweiguang.http.client.sse.SSEListener;
import io.github.kongweiguang.http.client.ws.WSListener;
import io.github.kongweiguang.http.common.core.Method;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.http.common.utils.HttpClientUtil.fixUrl;
import static java.util.Objects.nonNull;

/**
 * kong-http 的不可变请求规格。
 */
public final class HttpRequestSpec {
    private final ReqType reqType;
    private final Method method;
    private final String url;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;
    private final Map<String, Object> query;
    private final Map<String, Object> encodedQuery;
    private final String fragment;
    private final String encodedFragment;
    private final String contentType;
    private final Charset charset;
    private final byte[] body;
    private final Map<String, String> form;
    private final List<FilePart> files;
    private final Map<Object, Object> attrs;
    private final Conf conf;
    private final Consumer<Res> onSuccess;
    private final Consumer<Throwable> onFailure;
    private final RetryableTask<Res> retry;
    private final SSEListener sseListener;
    private final WSListener wsListener;

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

    public ReqType reqType() { return reqType; }
    public Method method() { return method; }
    public String url() { return url; }
    public Map<String, String> headers() { return headers; }
    public Map<String, String> cookies() { return cookies; }
    public Map<String, Object> query() { return query; }
    public Map<String, Object> encodedQuery() { return encodedQuery; }
    public String fragment() { return fragment; }
    public String encodedFragment() { return encodedFragment; }
    public String contentType() { return contentType; }
    public Charset charset() { return charset; }
    public byte[] body() { return body == null ? null : Arrays.copyOf(body, body.length); }
    public Map<String, String> form() { return form; }
    public List<FilePart> files() { return files; }
    public Conf conf() { return conf; }
    public Consumer<Res> onSuccess() { return onSuccess; }
    public Consumer<Throwable> onFailure() { return onFailure; }
    public RetryableTask<Res> retry() { return retry; }
    public SSEListener sseListener() { return sseListener; }
    public WSListener wsListener() { return wsListener; }
    @SuppressWarnings("unchecked")
    public <T> T attr(Object key) { return (T) attrs.get(key); }

    /**
     * 用于构建不可变请求规格的可变构建器。
     */
    public static final class Builder {
        private ReqType reqType = ReqType.http;
        private Method method = Method.GET;
        private String url = "http://localhost";
        private final Map<String, String> headers = new LinkedHashMap<>();
        private final Map<String, String> cookies = new LinkedHashMap<>();
        private final Map<String, Object> query = new LinkedHashMap<>();
        private final Map<String, Object> encodedQuery = new LinkedHashMap<>();
        private String fragment;
        private String encodedFragment;
        private String contentType;
        private Charset charset = StandardCharsets.UTF_8;
        private byte[] body;
        private final Map<String, String> form = new LinkedHashMap<>();
        private final List<FilePart> files = new ArrayList<>();
        private final Map<Object, Object> attrs = new HashMap<>();
        private Conf conf = Conf.global().copy();
        private Consumer<Res> onSuccess;
        private Consumer<Throwable> onFailure;
        private RetryableTask<Res> retry = RetryableTask.<Res>retryForPredicate(() -> null, (res, t) -> {
                    if (t != null) return Pair.of(true, res);
                    if (res != null) return Pair.of(!res.isOk(), res);
                    return Pair.of(false, res);
                })
                .maxAttempts(1);
        private SSEListener sseListener;
        private WSListener wsListener;

        public Builder reqType(ReqType reqType) { this.reqType = reqType; return this; }
        public Builder method(Method method) { this.method = method; return this; }
        public Builder url(String url) {
            notNull(url, "url 不能为空");
            this.url = fixUrl(url.trim());
            return this;
        }
        public Builder header(String name, String value) { if (nonNull(name) && nonNull(value)) headers.put(name, value); return this; }
        public Builder addHeader(String name, String value) { if (nonNull(name) && nonNull(value)) headers.merge(name, value, (o, n) -> n); return this; }
        public Builder headers(Map<String, String> headers) { if (headers != null) headers.forEach(this::header); return this; }
        public Builder cookie(String key, String value) { if (nonNull(key) && nonNull(value)) cookies.put(key, value); return this; }
        public Builder cookies(Map<String, String> cookies) { if (cookies != null) this.cookies.putAll(cookies); return this; }
        public Builder query(String key, Object value) { if (nonNull(key) && nonNull(value)) query.put(key, value); return this; }
        public Builder encodedQuery(String key, Object value) { if (nonNull(key) && nonNull(value)) encodedQuery.put(key, value); return this; }
        public Builder fragment(String fragment) { this.fragment = fragment; return this; }
        public Builder encodedFragment(String fragment) { this.encodedFragment = fragment; return this; }
        public Builder contentType(String contentType) { this.contentType = contentType; return this; }
        public Builder charset(Charset charset) { this.charset = charset; return this; }
        public Builder body(byte[] body) { this.body = body == null ? null : Arrays.copyOf(body, body.length); return this; }
        public Builder body(String body) { this.body = body == null ? null : body.getBytes(charset); return this; }
        public Builder body(String body, String contentType) { return body(body).contentType(contentType); }
        public Builder form(String key, Object value) { if (nonNull(key) && nonNull(value)) form.put(key, String.valueOf(value)); return this; }
        public Builder form(Map<String, Object> formMap) {
            if (formMap != null) formMap.forEach((k, v) -> form(k, v));
            return this;
        }
        public Builder file(String name, String fileName, byte[] bytes) {
            notNull(name, "name 不能为空");
            notNull(fileName, "fileName 不能为空");
            notNull(bytes, "bytes 不能为空");
            files.add(new FilePart(name, fileName, Arrays.copyOf(bytes, bytes.length)));
            return this;
        }
        public Builder attr(Object key, Object value) { attrs.put(key, value); return this; }
        public Builder config(Consumer<Conf> confConsumer) {
            notNull(confConsumer, "conf consumer 不能为空");
            confConsumer.accept(conf);
            return this;
        }
        public Builder success(Consumer<Res> success) { this.onSuccess = success; return this; }
        public Builder fail(Consumer<Throwable> fail) { this.onFailure = fail; return this; }
        public Builder retry(Consumer<RetryableTask<Res>> retryConsumer) {
            notNull(retryConsumer, "retry consumer 不能为空");
            retryConsumer.accept(retry);
            return this;
        }
        public Builder sseListener(SSEListener listener) { this.sseListener = listener; return this; }
        public Builder wsListener(WSListener listener) { this.wsListener = listener; return this; }

        public HttpRequestSpec build() { return new HttpRequestSpec(this); }
    }

    public static final class FilePart {
        private final String name;
        private final String fileName;
        private final byte[] bytes;

        public FilePart(String name, String fileName, byte[] bytes) {
            this.name = name;
            this.fileName = fileName;
            this.bytes = bytes;
        }

        public String name() { return name; }
        public String fileName() { return fileName; }
        public byte[] bytes() { return bytes; }
    }
}


