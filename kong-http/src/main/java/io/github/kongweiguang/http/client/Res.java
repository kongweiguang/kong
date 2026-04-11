package io.github.kongweiguang.http.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.kongweiguang.core.utils.IoUtil;
import io.github.kongweiguang.http.client.consts.Header;
import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;
import io.github.kongweiguang.json.Json;
import kotlin.Pair;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Opt.ofNullable;
import static io.github.kongweiguang.json.Json.*;
import static java.nio.file.Files.copy;

/**
 * http的响应
 *
 * @author kongweiguang
 */
public class Res implements AutoCloseable {

    /**
     * 保存底层 OkHttp response 对象
     */
    private final Response raw;


    /**
     * 创建 Res instance
     */
    private Res(Response resp) {
        this.raw = resp;
    }


    /**
     * 根据给定对象创建 wrapper instance
     */
    public static Res of(Response resp) {
        return new Res(resp);
    }


    /**
     * 返回底层 raw object
     */
    public Response raw() {
        return raw;
    }


    /**
     * 返回或设置 body content
     */
    public ResponseBody body() {
        return ofNullable(raw().body()).orElse(Util.EMPTY_RESPONSE);
    }


    /**
     * 返回 code
     */
    public int code() {
        return raw().code();
    }


    /**
     * 判断是否为 ok
     */
    public boolean isOk() {
        return raw().isSuccessful();
    }


    /**
     * 判断是否为 redirect
     */
    public boolean isRedirect() {
        return raw().isRedirect();
    }


    /**
     * 根据 name 返回 header value
     */
    public String header(String name) {
        return raw().header(name);
    }


    /**
     * 返回 headers
     */
    public Map<String, List<String>> headers() {
        Headers headers = raw().headers();

        Map<String, List<String>> fr = new HashMap<>(headers.size(), 1);

        for (Pair<? extends String, ? extends String> hd : headers) {
            fr.computeIfAbsent(hd.getFirst(), k -> new ArrayList<>()).add(hd.getSecond());
        }

        return fr;
    }


    /**
     * 返回 contentType
     */
    public String contentType() {
        return body().contentType().toString();
    }


    /**
     * 返回 charset
     */
    public Charset charset() {
        return body().contentType().charset();
    }


    /**
     * 返回 contentEncoding
     */
    public String contentEncoding() {
        return header(Header.CONTENT_ENCODING.v());
    }


    /**
     * 返回 contentLength
     */
    public long contentLength() {
        return body().contentLength();
    }


    /**
     * 返回 cookieStr string
     */
    public String cookieStr() {
        return header(Header.COOKIE.v());
    }


    /**
     * 返回 cookies
     */
    public List<Cookie> cookies() {
        return Cookie.parseAll(raw().request().url(), raw().headers());
    }


    /**
     * 返回 request duration（milliseconds）
     */
    public long useMillis() {
        return raw().receivedResponseAtMillis() - raw().sentRequestAtMillis();
    }


    /**
     * 以 byte[] 形式返回 content
     */
    public byte[] bytes() {
        try {
            return body().bytes();
        } catch (IOException e) {
            throw new KongHttpRuntimeException(e);
        }
    }


    /**
     * 以 string 形式返回 content
     */
    public String str() {
        try {
            return body().string();
        } catch (IOException e) {
            throw new KongHttpRuntimeException(e);
        }
    }


    /**
     * 以 string 形式返回 content
     */
    public String str(Charset charset) {
        return new String(bytes(), charset);
    }


    /**
     * 以 input stream 形式返回 content
     */
    public InputStream stream() {
        return body().byteStream();
    }


    /**
     * 将 content 解析为 JSON node
     */
    public JsonNode node() {
        return Json.toNode(str());
    }


    /**
     * 将 content 解析为 object
     */
    public <R> R obj(Class<R> clazz) {
        return toObj(str(), clazz);
    }


    /**
     * 将 content 解析为 object，失败时返回 default value
     */
    public <R> R defaultObj(Class<R> clazz, R r) {
        try {
            return toObj(str(), clazz);
        } catch (Exception e) {
            return r;
        }
    }


    /**
     * 将 content 解析为 object
     */
    public <R> R obj(TypeReference<R> typeRef) {
        return toObj(str(), typeRef);
    }


    /**
     * 将 content 解析为 object，失败时返回 default value
     */
    public <R> R defaultObj(TypeReference<R> typeRef, R r) {
        try {
            return toObj(str(), typeRef);

        } catch (Exception e) {
            return r;
        }
    }


    /**
     * 将 content 解析为 integer
     */
    public Integer i32() {
        return obj(Integer.class);
    }


    /**
     * 将 content 解析为 long
     */
    public Long i64() {
        return obj(Long.class);
    }


    /**
     * 将 content 解析为 boolean
     */
    public Boolean bool() {
        return obj(Boolean.class);
    }


    /**
     * 将 content 解析为 list
     */
    public <E> List<E> list(Class<E> clazz) {
        return toList(str(), clazz);
    }


    /**
     * 将 content 解析为 list
     */
    public <E> List<E> list(TypeReference<List<E>> typeRef) {
        return toList(str(), typeRef);
    }


    /**
     * 将 content 解析为 map
     */
    public <K, V> Map<K, V> map(Class<K> k, Class<V> v) {
        return toMap(str(), k, v);
    }


    /**
     * 将 content 解析为 map
     */
    public <K, V> Map<K, V> map(TypeReference<Map<K, V>> typeRef) {
        return toMap(str(), typeRef);
    }


    /**
     * 将 content 写入指定 file
     */
    public long file(String path, CopyOption... options) throws IOException {
        return copy(stream(), Paths.get(path), options);
    }


    /**
     * 对当前 response 执行附加处理
     */
    public Res then(Consumer<Res> con) {
        ofNullable(con).ifPresent(c -> c.accept(this));
        return this;
    }


    /**
     * 关闭底层 resource
     */
    @Override
    public void close() {
        IoUtil.close(raw());
    }


    /**
     * 返回当前对象的 string
     */
    @Override
    public String toString() {
        return raw().toString();
    }
}
