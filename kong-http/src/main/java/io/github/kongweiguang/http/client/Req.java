package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Method;

/**
 * 请求入口。
 */
public final class Req {

    private Req() {
    }

    /**
     * 创建默认实例。
     */
    public static HttpRequestSpec.Builder of() {
        return new HttpRequestSpec.Builder();
    }

    /**
     * 创建默认实例。
     */
    public static HttpRequestSpec.Builder of(String url) {
        return of().url(url);
    }

    /**
     * 执行get 相关操作。
     */
    public static HttpRequestSpec.Builder get(String url) {
        return method(Method.GET, url);
    }

    /**
     * 执行post 相关操作。
     */
    public static HttpRequestSpec.Builder post(String url) {
        return method(Method.POST, url);
    }

    /**
     * 执行delete 相关操作。
     */
    public static HttpRequestSpec.Builder delete(String url) {
        return method(Method.DELETE, url);
    }

    /**
     * 执行put 相关操作。
     */
    public static HttpRequestSpec.Builder put(String url) {
        return method(Method.PUT, url);
    }

    /**
     * 执行patch 相关操作。
     */
    public static HttpRequestSpec.Builder patch(String url) {
        return method(Method.PATCH, url);
    }

    /**
     * 执行head 相关操作。
     */
    public static HttpRequestSpec.Builder head(String url) {
        return method(Method.HEAD, url);
    }

    /**
     * 执行options 相关操作。
     */
    public static HttpRequestSpec.Builder options(String url) {
        return method(Method.OPTIONS, url);
    }

    /**
     * 执行trace 相关操作。
     */
    public static HttpRequestSpec.Builder trace(String url) {
        return method(Method.TRACE, url);
    }

    /**
     * 执行connect 相关操作。
     */
    public static HttpRequestSpec.Builder connect(String url) {
        return method(Method.CONNECT, url);
    }

    /**
     * 执行formUrlencoded 相关操作。
     */
    public static HttpRequestSpec.Builder formUrlencoded(String url) {
        return post(url).contentType(ContentType.FORM_URLENCODED.v());
    }

    /**
     * 执行multipart 相关操作。
     */
    public static HttpRequestSpec.Builder multipart(String url) {
        return post(url).contentType(ContentType.MULTIPART.v());
    }

    /**
     * 创建 WebSocket 请求构建器。
     */
    public static HttpRequestSpec.Builder ws(String url) {
        return KongHttpClient.ws(url);
    }

    /**
     * 创建 SSE 请求构建器。
     */
    public static HttpRequestSpec.Builder sse(String url) {
        return KongHttpClient.sse(url);
    }

    private static HttpRequestSpec.Builder method(Method method, String url) {
        return of(url).method(method);
    }
}

