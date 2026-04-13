package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Header;
import io.github.kongweiguang.http.client.consts.Method;
import io.github.kongweiguang.http.client.pipeline.RequestPipeline;
import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import okhttp3.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class RequestPipelineTest {

    @Test
    void shouldAppendCharsetForRawContentType() {
        HttpReqSpec spec = Req.of("http://localhost/post")
                .method(Method.POST)
                .contentType(ContentType.JSON.value())
                .charset(StandardCharsets.UTF_16)
                .json("{}");

        Request request = RequestPipeline.http().build(spec);

        Assertions.assertEquals(ContentType.JSON.value() + ";charset=" + StandardCharsets.UTF_16.name(),
                request.header(Header.CONTENT_TYPE.value()));
    }

    @Test
    void shouldSkipCookieHeaderWhenEmpty() {
        HttpReqSpec spec = Req.get("http://localhost/get");

        Request request = RequestPipeline.http().build(spec);

        Assertions.assertNull(request.header(Header.COOKIE.value()));
    }

    @Test
    void shouldEmitCookieHeaderWithoutTrailingSeparator() {
        HttpReqSpec spec = Req.get("http://localhost/get")
                .cookie("a", "1")
                .cookie("b", "2");

        Request request = RequestPipeline.http().build(spec);

        Assertions.assertEquals("a=1; b=2", request.header(Header.COOKIE.value()));
    }

    @Test
    void shouldComposeUrlFromMutableBuilderBeforeSend() {
        HttpReqSpec spec = Req.of("example.com/api")
                .scheme("https")
                .host("service.local")
                .port(8443)
                .path("v1/orders")
                .query("status", "paid")
                .fragment("detail");

        Assertions.assertEquals("https://service.local:8443/api/v1/orders", spec.urlBuilder().toString());

        Request request = RequestPipeline.http().build(spec);

        Assertions.assertEquals("https://service.local:8443/api/v1/orders?status=paid#detail",
                request.url().toString());
    }
}
