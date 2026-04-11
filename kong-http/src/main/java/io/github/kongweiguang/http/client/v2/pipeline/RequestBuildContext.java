package io.github.kongweiguang.http.client.v2.pipeline;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Mutable context shared by request build steps.
 */
public final class RequestBuildContext {
    private final HttpRequestSpec spec;
    private final Request.Builder builder = new Request.Builder();
    private RequestBody requestBody;
    private Request request;

    public RequestBuildContext(HttpRequestSpec spec) {
        this.spec = spec;
    }

    public HttpRequestSpec spec() { return spec; }
    public Request.Builder builder() { return builder; }
    public RequestBody requestBody() { return requestBody; }
    public void requestBody(RequestBody requestBody) { this.requestBody = requestBody; }
    public Request request() { return request; }
    public void request(Request request) { this.request = request; }
}

