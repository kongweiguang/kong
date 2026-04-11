package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 请求构建阶段上下文对象。
 *
 * @author kongweiguang
 */
public final class RequestBuildContext {

    /**
     * 保存 spec
     */
    private final HttpRequestSpec spec;

    /**
     * 保存 builder
     */
    private final Request.Builder builder = new Request.Builder();

    /**
     * 保存 request body
     */
    private RequestBody requestBody;

    /**
     * 保存 request
     */
    private Request request;


    /**
     * 创建 RequestBuildContext instance
     */
    public RequestBuildContext(HttpRequestSpec spec) {
        this.spec = spec;
    }


    /**
     * 返回 spec
     */
    public HttpRequestSpec spec() {
        return spec;
    }

    /**
     * 返回 request builder。
     */
    public Request.Builder builder() {
        return builder;
    }


    /**
     * 返回 request body
     */
    public RequestBody requestBody() {
        return requestBody;
    }


    /**
     * 执行 request body 操作
     */
    public void requestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }


    /**
     * 返回 request
     */
    public Request request() {
        return request;
    }


    /**
     * 执行 request 操作
     */
    public void request(Request request) {
        this.request = request;
    }
}

