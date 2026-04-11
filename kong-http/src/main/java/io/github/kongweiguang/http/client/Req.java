package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.builder.DefHTTPReqBuilder;
import io.github.kongweiguang.http.client.builder.SSEReqBuilder;
import io.github.kongweiguang.http.client.builder.WSReqBuilder;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Method;

/**
 * 基于okhttp封装的http请求工具
 *
 * @author kongweiguang
 */
public final class Req {

    private Req() {
    }

    //工厂方法
    public static DefHTTPReqBuilder of() {
        return new DefHTTPReqBuilder();
    }

    public static DefHTTPReqBuilder of(String url) {
        return of().url(url);
    }

    public static DefHTTPReqBuilder get(String url) {
        return method(Method.GET, url);
    }

    public static DefHTTPReqBuilder post(String url) {
        return method(Method.POST, url);
    }

    public static DefHTTPReqBuilder delete(String url) {
        return method(Method.DELETE, url);
    }

    public static DefHTTPReqBuilder put(String url) {
        return method(Method.PUT, url);
    }

    public static DefHTTPReqBuilder patch(String url) {
        return method(Method.PATCH, url);
    }

    public static DefHTTPReqBuilder head(String url) {
        return method(Method.HEAD, url);
    }

    public static DefHTTPReqBuilder options(String url) {
        return method(Method.OPTIONS, url);
    }

    public static DefHTTPReqBuilder trace(String url) {
        return method(Method.TRACE, url);
    }

    public static DefHTTPReqBuilder connect(String url) {
        return method(Method.CONNECT, url);
    }

    public static DefHTTPReqBuilder formUrlencoded(String url) {
        return post(url).contentType(ContentType.FORM_URLENCODED.v());
    }

    public static DefHTTPReqBuilder multipart(String url) {
        return post(url).contentType(ContentType.MULTIPART.v());
    }

    //ws
    public static WSReqBuilder ws(String url) {
        return new WSReqBuilder().url(url);
    }

    //sse
    public static SSEReqBuilder sse(String url) {
        return new SSEReqBuilder().url(url);
    }

    private static DefHTTPReqBuilder method(Method method, String url) {
        return of(url).method(method);
    }
}
