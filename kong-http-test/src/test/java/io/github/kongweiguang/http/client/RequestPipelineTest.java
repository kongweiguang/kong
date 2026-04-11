package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Header;
import io.github.kongweiguang.http.client.consts.Method;
import okhttp3.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class RequestPipelineTest {

    @Test
    void shouldAppendCharsetForRawContentType() {
        HttpRequestSpec spec = Req.of("http://localhost/post")
                .method(Method.POST)
                .contentType(ContentType.JSON.v())
                .charset(StandardCharsets.UTF_16)
                .body("{}")
                ;

        Request request = new RequestPipeline().build(spec);

        Assertions.assertEquals(ContentType.JSON.v() + ";charset=" + StandardCharsets.UTF_16.name(),
                request.header(Header.CONTENT_TYPE.v()));
    }

    @Test
    void shouldSkipCookieHeaderWhenEmpty() {
        HttpRequestSpec spec = Req.get("http://localhost/get");

        Request request = new RequestPipeline().build(spec);

        Assertions.assertNull(request.header(Header.COOKIE.v()));
    }

    @Test
    void shouldEmitCookieHeaderWithoutTrailingSeparator() {
        HttpRequestSpec spec = Req.get("http://localhost/get")
                .cookie("a", "1")
                .cookie("b", "2")
                ;

        Request request = new RequestPipeline().build(spec);

        Assertions.assertEquals("a=1; b=2", request.header(Header.COOKIE.v()));
    }

    @Test
    void shouldComposeUrlFromMutableBuilderBeforeSend() {
        HttpRequestSpec spec = Req.of("example.com/api")
                .scheme("https")
                .host("service.local")
                .port(8443)
                .path("v1/orders")
                .query("status", "paid")
                .fragment("detail");

        Assertions.assertEquals("https://service.local:8443/api/v1/orders", spec.url());

        Request request = new RequestPipeline().build(spec);

        Assertions.assertEquals("https://service.local:8443/api/v1/orders?status=paid#detail",
                request.url().toString());
    }
}
