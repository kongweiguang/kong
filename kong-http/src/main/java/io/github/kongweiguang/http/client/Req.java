package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Method;

/**
 * 请求入口。
 */
public final class Req {

    private Req() {
    }

    public static HttpRequestSpec.Builder of() {
        return new HttpRequestSpec.Builder();
    }

    public static HttpRequestSpec.Builder of(String url) {
        return of().url(url);
    }

    public static HttpRequestSpec.Builder get(String url) {
        return method(Method.GET, url);
    }

    public static HttpRequestSpec.Builder post(String url) {
        return method(Method.POST, url);
    }

    public static HttpRequestSpec.Builder delete(String url) {
        return method(Method.DELETE, url);
    }

    public static HttpRequestSpec.Builder put(String url) {
        return method(Method.PUT, url);
    }

    public static HttpRequestSpec.Builder patch(String url) {
        return method(Method.PATCH, url);
    }

    public static HttpRequestSpec.Builder head(String url) {
        return method(Method.HEAD, url);
    }

    public static HttpRequestSpec.Builder options(String url) {
        return method(Method.OPTIONS, url);
    }

    public static HttpRequestSpec.Builder trace(String url) {
        return method(Method.TRACE, url);
    }

    public static HttpRequestSpec.Builder connect(String url) {
        return method(Method.CONNECT, url);
    }

    public static HttpRequestSpec.Builder formUrlencoded(String url) {
        return post(url).contentType(ContentType.FORM_URLENCODED.v());
    }

    public static HttpRequestSpec.Builder multipart(String url) {
        return post(url).contentType(ContentType.MULTIPART.v());
    }

    public static HttpRequestSpec.Builder ws(String url) {
        return KongHttpClient.ws(url);
    }

    public static HttpRequestSpec.Builder sse(String url) {
        return KongHttpClient.sse(url);
    }

    private static HttpRequestSpec.Builder method(Method method, String url) {
        return of(url).method(method);
    }
}

