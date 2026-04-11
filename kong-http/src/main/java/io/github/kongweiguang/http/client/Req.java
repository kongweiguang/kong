package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Method;
import io.github.kongweiguang.http.client.core.ReqType;

/**
 * 请求入口
 */
public final class Req {

    /**
     * 创建 Req instance
     */
    private Req() {
    }

    /**
     * 创建一个空的 HTTP 请求构建器
     *
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder of() {
        return new HttpRequestSpec.Builder();
    }

    /**
     * 创建一个带目标地址的 HTTP 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder of(String url) {
        return of().url(url);
    }

    /**
     * 创建 GET 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder get(String url) {
        return method(Method.GET, url);
    }

    /**
     * 创建 POST 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder post(String url) {
        return method(Method.POST, url);
    }

    /**
     * 创建 DELETE 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder delete(String url) {
        return method(Method.DELETE, url);
    }

    /**
     * 创建 PUT 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder put(String url) {
        return method(Method.PUT, url);
    }

    /**
     * 创建 PATCH 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder patch(String url) {
        return method(Method.PATCH, url);
    }

    /**
     * 创建 HEAD 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder head(String url) {
        return method(Method.HEAD, url);
    }

    /**
     * 创建 OPTIONS 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder options(String url) {
        return method(Method.OPTIONS, url);
    }

    /**
     * 创建 TRACE 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder trace(String url) {
        return method(Method.TRACE, url);
    }

    /**
     * 创建 CONNECT 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder connect(String url) {
        return method(Method.CONNECT, url);
    }

    /**
     * 创建一个以 {@code application/x-www-form-urlencoded} 作为内容类型的 POST 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder formUrlencoded(String url) {
        return post(url).contentType(ContentType.FORM_URLENCODED.v());
    }

    /**
     * 创建一个以 {@code multipart/form-data} 作为内容类型的 POST 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder multipart(String url) {
        return post(url).contentType(ContentType.MULTIPART.v());
    }

    /**
     * 创建 WebSocket 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder ws(String url) {
        return get(url)
                .reqType(ReqType.ws)
                .method(Method.GET);
    }

    /**
     * 创建 SSE 请求构建器
     *
     * @param url 请求地址
     * @return 请求构建器
     */
    public static HttpRequestSpec.Builder sse(String url) {
        return get(url)
                .reqType(ReqType.sse)
                .method(Method.GET)
                .contentType(ContentType.EVENT_STREAM.v());
    }

    private static HttpRequestSpec.Builder method(Method method, String url) {
        return of(url).method(method);
    }
}

