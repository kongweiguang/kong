package io.github.kongweiguang.http.client;


import io.github.kongweiguang.http.client.core.Method;
import io.github.kongweiguang.http.client.core.ReqTypeEnum;

/**
 * 基于okhttp封装的http请求工具
 *
 * @author kongweiguang
 */
public class Req {

    //工厂方法
    public static ReqBuilder of() {
        return new ReqBuilder();
    }

    public static ReqBuilder of(String url) {
        return of().url(url);
    }

    public static ReqBuilder get(String url) {
        return of(url).method(Method.GET);
    }

    public static ReqBuilder post(String url) {
        return of(url).method(Method.POST);
    }

    public static ReqBuilder delete(String url) {
        return of(url).method(Method.DELETE);
    }

    public static ReqBuilder put(String url) {
        return of(url).method(Method.PUT);
    }

    public static ReqBuilder patch(String url) {
        return of(url).method(Method.PATCH);
    }

    public static ReqBuilder head(String url) {
        return of(url).method(Method.HEAD);
    }

    public static ReqBuilder options(String url) {
        return of(url).method(Method.OPTIONS);
    }

    public static ReqBuilder trace(String url) {
        return of(url).method(Method.TRACE);
    }

    public static ReqBuilder connect(String url) {
        return of(url).method(Method.CONNECT);
    }

    public static ReqBuilder formUrlencoded(String url) {
        return of(url).formUrlencoded();
    }

    public static ReqBuilder multipart(String url) {
        return of(url).multipart();
    }

    //ws
    public static ReqBuilder ws(String url) {
        return of().reqType(ReqTypeEnum.ws).url(url);
    }

    //sse
    public static ReqBuilder sse(String url) {
        return of().reqType(ReqTypeEnum.sse).url(url);
    }


}
