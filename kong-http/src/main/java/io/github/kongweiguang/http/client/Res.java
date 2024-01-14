package io.github.kongweiguang.http.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.kongweiguang.http.client.core.Header;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.kongweiguang.json.Json.*;
import static java.nio.file.Files.copy;
import static java.util.Optional.ofNullable;
import static okhttp3.internal.Util.closeQuietly;

/**
 * http的响应
 *
 * @author kongweiguang
 */
public final class Res implements AutoCloseable {

    //原始res对象
    private final Response raw;

    private Res(final Response resp) {
        this.raw = resp;
    }

    /**
     * 工厂方法
     *
     * @param resp 原始响应对象
     * @return Res对象 {@link Res}
     */
    public static Res of(final Response resp) {
        return new Res(resp);
    }

    /**
     * 获得响应的原始对象
     *
     * @return 原始对象 {@link Response}
     */
    public Response raw() {
        return raw;
    }

    /**
     * 响应体
     *
     * @return 原始响应体 {@link ResponseBody}
     */
    public ResponseBody body() {
        return ofNullable(raw().body()).orElse(Util.EMPTY_RESPONSE);
    }

    /**
     * 响应code
     *
     * @return code
     */
    public int code() {
        return raw().code();
    }

    /**
     * 请求是否成功
     *
     * @return 是否成功
     */
    public boolean isOk() {
        return raw().isSuccessful();
    }

    /**
     * 是否重定向
     *
     * @return 是否重定向
     */
    public boolean isRedirect() {
        return raw().isRedirect();
    }

    /**
     * 根据名称获得响应头
     *
     * @param name 响应头名称
     * @return 响应头值
     */
    public String header(final String name) {
        return raw().header(name);
    }

    /**
     * 获得响应头
     *
     * @return 响应头集合
     */
    public Map<String, List<String>> headers() {
        final Headers headers = raw().headers();

        final Map<String, List<String>> fr = new HashMap<>(headers.size(), 1);

        for (final Pair<? extends String, ? extends String> hd : headers) {
            fr.computeIfAbsent(hd.getFirst(), k -> new ArrayList<>()).add(hd.getSecond());
        }

        return fr;
    }

    /**
     * 获得响应的contentType
     *
     * @return contentType
     */
    public String contentType() {
        return body().contentType().toString();
    }

    /**
     * 获得响应的charset
     *
     * @return charset
     */
    public Charset charset() {
        return body().contentType().charset(StandardCharsets.UTF_8);
    }

    /**
     * 获得响应的contentEncoding
     *
     * @return contentEncoding
     */
    public String contentEncoding() {
        return header(Header.content_encoding.v());
    }

    /**
     * 获得响应的contentLength
     *
     * @return contentLength
     */
    public long contentLength() {
        return body().contentLength();
    }

    /**
     * 获得响应的cookie
     *
     * @return cookie
     */
    public String cookieStr() {
        return header(Header.cookie.v());
    }

    /**
     * 获取cookie集合
     *
     * @return cookie集合
     */
    public List<Cookie> cookies() {
        return Cookie.parseAll(raw().request().url(), raw().headers());
    }

    /**
     * 请求使用时间
     *
     * @return 使用时间
     */
    public long useMillis() {
        return raw().receivedResponseAtMillis() - raw().sentRequestAtMillis();
    }

    /**
     * 获得响应的body，byte
     *
     * @return 消息体字节数组
     */
    public byte[] bytes() {
        try {
            return body().bytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得响应的body，String
     *
     * @return 消息体字符串
     */
    public String str() {
        try {
            return body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得响应的body，String
     *
     * @param charset 字符集
     * @return 消息体字符串
     */
    public String str(Charset charset) {
        return new String(bytes(), charset);
    }

    /**
     * 获得响应的body，InputStream
     *
     * @return 相应体流
     */
    public InputStream stream() {
        return body().byteStream();
    }

    /**
     * 获得相应的body，jsonNode
     *
     * @return json节点 {@link  JsonNode}
     */
    public JsonNode node() {
        return Json.toNode(str());
    }

    /**
     * 获得响应对象，根据据类型转换
     *
     * @param clazz 目标类型class
     * @param <R>   目标类型
     * @return 响应对象
     */
    public <R> R obj(Class<R> clazz) {
        return toObj(str(), clazz);
    }

    /**
     * 获得响应对象，根据据类型转换
     *
     * @param clazz 目标类型class
     * @param r     默认值
     * @param <R>   目标类型
     * @return 响应对象
     */
    public <R> R defaultObj(Class<R> clazz, R r) {
        try {
            return toObj(str(), clazz);
        } catch (Exception e) {
            return r;
        }
    }

    /**
     * 获得响应对象，根据据类型转换
     *
     * @param typeRef 类型
     * @param <R>     目标类型
     * @return 响应对象
     */
    public <R> R obj(TypeReference<R> typeRef) {
        return toObj(str(), typeRef);
    }

    /**
     * 获得响应对象，根据据类型转换
     *
     * @param typeRef 类型
     * @param r       默认值
     * @param <R>     目标类型
     * @return 响应对象
     */
    public <R> R defaultObj(TypeReference<R> typeRef, R r) {
        try {
            return toObj(str(), typeRef);
        } catch (Exception e) {
            return r;
        }
    }

    /**
     * 获得int类型结果
     *
     * @return 响应对象
     */
    public Integer i32() {
        return obj(Integer.class);
    }

    /**
     * 获得long类型结果
     *
     * @return 响应对象
     */
    public Long i64() {
        return obj(Long.class);
    }

    /**
     * 获得bool类型响应对象
     *
     * @return 响应对象
     */
    public Boolean bool() {
        return obj(Boolean.class);
    }

    /**
     * 返回list类型结果
     *
     * @param <E> 集合中的元素
     * @return 响应对象
     */
    public <E> List<E> list() {
        return toList(str());
    }

    /**
     * 返回map类型结果
     *
     * @param <K> Map的key
     * @param <V> Map的Value
     * @return 当前对象 {@link Res}
     */
    public <K, V> Map<K, V> map() {
        return toMap(str());
    }

    /**
     * 保存文件
     *
     * @param path    文件路径
     * @param options 文件选项
     * @return 读取或写入的字节数
     * @throws IOException IOException
     */
    public long file(String path, CopyOption... options) throws IOException {
        return copy(stream(), Paths.get(path), options);
    }

    /**
     * 使用链式编程消费
     *
     * @param con 操作
     * @return 当前对象 {@link Res}
     */
    public Res then(Consumer<Res> con) {
        ofNullable(con).ifPresent(c -> c.accept(this));
        return this;
    }

    /**
     * 关闭res对象
     */
    @Override
    public void close() {
        closeQuietly(raw());
    }

    @Override
    public String toString() {
        return raw().toString();
    }
}
