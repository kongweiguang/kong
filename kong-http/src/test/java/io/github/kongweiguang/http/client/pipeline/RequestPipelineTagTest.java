package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import io.github.kongweiguang.http.client.spec.SseReqSpec;
import io.github.kongweiguang.http.client.spec.WsReqSpec;
import org.junit.jupiter.api.Test;

import static io.github.kongweiguang.http.client.consts.Method.POST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class RequestPipelineTagTest {

    @Test
    void shouldTagHttpRequestWithHttpSpec() {
        HttpReqSpec spec = new HttpReqSpec().url("https://example.com");

        var request = RequestPipeline.http().build(spec);

        assertSame(spec, request.tag(HttpReqSpec.class));
    }

    @Test
    void shouldTagSseRequestWithSseSpec() {
        SseReqSpec spec = new SseReqSpec().url("https://example.com/sse");

        var request = RequestPipeline.sse().build(spec);

        assertSame(spec, request.tag(SseReqSpec.class));
    }

    @Test
    void shouldApplyMethodBodyAndContentTypeForSseSpec() {
        SseReqSpec spec = new SseReqSpec()
                .url("https://example.com/sse")
                .method(POST)
                .body("hello", "text/plain");

        var request = RequestPipeline.sse().build(spec);

        assertEquals("POST", request.method());
        assertEquals("text/plain;charset=UTF-8", request.header("Content-Type"));
        assertNotNull(request.body());
    }

    @Test
    void shouldTagWsRequestWithWsSpec() {
        WsReqSpec spec = new WsReqSpec().url("ws://example.com/ws");

        var request = RequestPipeline.ws().build(spec);

        assertSame(spec, request.tag(WsReqSpec.class));
    }
}
