package io.github.kongweiguang.http.client;


import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Method;
import io.github.kongweiguang.http.client.core.ReqType;
import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import io.github.kongweiguang.http.client.spec.SseReqSpec;
import io.github.kongweiguang.http.client.spec.WsReqSpec;

/**
 * 基于okhttp封装的http请求工具
 *
 * @author kongweiguang
 */
public final class Req {

    private Req() {
    }

    /**
     * 创建 HTTP 请求构建器。
     *
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec of() {
        return new HttpReqSpec();
    }

    /**
     * 创建 HTTP 请求构建器。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec of(String url) {
        return of().url(url);
    }

    /**
     * 创建 GET 请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec get(String url) {
        return method(Method.GET, url);
    }

    /**
     * 创建 POST 请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec post(String url) {
        return method(Method.POST, url);
    }

    /**
     * 创建 DELETE 请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec delete(String url) {
        return method(Method.DELETE, url);
    }

    /**
     * 创建 PUT 请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec put(String url) {
        return method(Method.PUT, url);
    }

    /**
     * 创建 PATCH 请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec patch(String url) {
        return method(Method.PATCH, url);
    }

    /**
     * 创建 HEAD 请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec head(String url) {
        return method(Method.HEAD, url);
    }

    /**
     * 创建 OPTIONS 请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec options(String url) {
        return method(Method.OPTIONS, url);
    }

    /**
     * 创建 TRACE 请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec trace(String url) {
        return method(Method.TRACE, url);
    }

    /**
     * 创建 CONNECT 请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec connect(String url) {
        return method(Method.CONNECT, url);
    }

    /**
     * 创建 application/x-www-form-urlencoded 表单请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec formUrlencoded(String url) {
        return post(url).contentType(ContentType.FORM_URLENCODED.value());
    }

    /**
     * 创建 multipart/form-data 表单请求。
     *
     * @param url 请求地址
     * @return HTTP 请求构建器
     */
    public static HttpReqSpec multipart(String url) {
        return post(url).contentType(ContentType.MULTIPART.value());
    }

    /**
     * 按指定请求方法创建 HTTP 请求。
     *
     * @param method 请求方法
     * @param url    请求地址
     * @return HTTP 请求构建器
     */
    private static HttpReqSpec method(Method method, String url) {
        return of(url).method(method);
    }

    /**
     * 创建 WebSocket 请求。
     *
     * @param url 请求地址
     * @return WebSocket 请求构建器
     */
    public static WsReqSpec ws(String url) {
        return new WsReqSpec()
                .url(url)
                .reqType(ReqType.ws);
    }

    /**
     * 创建 SSE 请求。
     *
     * @param url 请求地址
     * @return SSE 请求构建器
     */
    public static SseReqSpec sse(String url) {
        return new SseReqSpec()
                .url(url)
                .reqType(ReqType.sse)
                .contentType(ContentType.EVENT_STREAM.value());
    }


}
