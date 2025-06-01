package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.builder.HttpReqBuilder;
import io.github.kongweiguang.http.client.builder.SSEReqBuilder;
import io.github.kongweiguang.http.client.builder.WSReqBuilder;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Method;

/**
 * 基于okhttp封装的http请求工具
 *
 * @author kongweiguang
 */
public class Req {
    private static class DefHTTPReqBuilder extends HttpReqBuilder<DefHTTPReqBuilder, Res> {
    }

    //工厂方法
    public static HttpReqBuilder<?, Res> of() {
        return new DefHTTPReqBuilder();
    }

    public static HttpReqBuilder<?, Res> of(String url) {
        return of().url(url);
    }

    public static HttpReqBuilder<?, Res> get(String url) {
        return of(url).method(Method.GET);
    }

    public static HttpReqBuilder<?, Res> post(String url) {
        return of(url).method(Method.POST);
    }

    public static HttpReqBuilder<?, Res> delete(String url) {
        return of(url).method(Method.DELETE);
    }

    public static HttpReqBuilder<?, Res> put(String url) {
        return of(url).method(Method.PUT);
    }

    public static HttpReqBuilder<?, Res> patch(String url) {
        return of(url).method(Method.PATCH);
    }

    public static HttpReqBuilder<?, Res> head(String url) {
        return of(url).method(Method.HEAD);
    }

    public static HttpReqBuilder<?, Res> options(String url) {
        return of(url).method(Method.OPTIONS);
    }

    public static HttpReqBuilder<?, Res> trace(String url) {
        return of(url).method(Method.TRACE);
    }

    public static HttpReqBuilder<?, Res> connect(String url) {
        return of(url).method(Method.CONNECT);
    }

    public static HttpReqBuilder<?, Res> formUrlencoded(String url) {
        return post(url).contentType(ContentType.FORM_URLENCODED.v());
    }

    public static HttpReqBuilder<?, Res> multipart(String url) {
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
}
