package io.github.kongweiguang.http.client.sse;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.builder.SSEReqBuilder;
import io.github.kongweiguang.http.client.core.ReqType;
import io.github.kongweiguang.http.common.sse.SseEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SseTest {

    @Test
    void testBuilder() {
        SSEListener listener = new SSEListener() {
            @Override
            public void event(SSEReqBuilder req, SseEvent msg) {
            }
        };

        SSEReqBuilder builder = Req.sse("http://localhost/sse")
                .query("k", "v")
                .header("h", "v")
                .sseListener(listener);

        Assertions.assertEquals(ReqType.sse, builder.reqType());
        Assertions.assertSame(listener, builder.sseListener());
        Assertions.assertEquals("localhost", builder.urlBuilder().build().host());
        Assertions.assertEquals("/sse", builder.urlBuilder().build().encodedPath());
    }

}
