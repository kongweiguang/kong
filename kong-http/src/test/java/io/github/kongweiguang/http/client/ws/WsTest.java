package io.github.kongweiguang.http.client.ws;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.builder.WSReqBuilder;
import io.github.kongweiguang.http.client.core.ReqType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WsTest {

    @Test
    public void testBuilder() {
        WSListener listener = new WSListener() {
        };

        WSReqBuilder builder = Req.ws("ws://localhost:8889/ws")
                .query("k", "v")
                .header("h", "v")
                .wsListener(listener);

        Assertions.assertEquals(ReqType.ws, builder.reqType());
        Assertions.assertSame(listener, builder.wsListener());
        Assertions.assertEquals("localhost", builder.urlBuilder().build().host());
        Assertions.assertEquals(8889, builder.urlBuilder().build().port());
        Assertions.assertEquals("/ws", builder.urlBuilder().build().encodedPath());
    }

}
