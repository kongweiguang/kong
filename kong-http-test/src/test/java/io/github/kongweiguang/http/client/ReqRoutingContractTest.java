package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.consts.Method;
import io.github.kongweiguang.http.client.core.ReqType;
import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;
import io.github.kongweiguang.http.client.spec.ReqSpec;
import io.github.kongweiguang.http.client.spec.SseReqSpec;
import io.github.kongweiguang.http.client.spec.WsReqSpec;
import io.github.kongweiguang.http.client.sse.SseEvent;
import io.github.kongweiguang.http.client.sse.SseListener;
import io.github.kongweiguang.http.client.ws.WsListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

class ReqRoutingContractTest {

    @Test
    void shouldInitializeReqTypeForWsAndSse() {
        WsReqSpec ws = Req.ws("ws://127.0.0.1:1/ws");
        SseReqSpec sse = Req.sse("http://127.0.0.1:1/sse");

        Assertions.assertEquals(ReqType.ws, ws.reqType());
        Assertions.assertEquals(ReqType.sse, sse.reqType());
    }

    @Test
    void shouldNotExposePublicReqTypeSetter() {
        boolean hasPublicReqTypeSetter = Arrays.stream(ReqSpec.class.getMethods())
                .anyMatch(m -> m.getName().equals("reqType")
                               && m.getParameterCount() == 1
                               && m.getParameterTypes()[0] == ReqType.class);

        Assertions.assertFalse(hasPublicReqTypeSetter);
    }

    @Test
    void shouldRouteByReqTypeEvenWhenMethodChanged() throws Exception {
        ExecutionException wsError = Assertions.assertThrows(ExecutionException.class, () -> Req.ws("ws://127.0.0.1:1/ws")
                .method(Method.POST)
                .wsListener(new WsListener() {
                })
                .<Object>okAsync()
                .get(2, TimeUnit.SECONDS));
        Assertions.assertInstanceOf(KongHttpRuntimeException.class, wsError.getCause());
        Assertions.assertTrue(wsError.getCause().getMessage().contains("Request must be GET"));

        try {
            Object value = Req.sse("http://127.0.0.1:1/sse")
                    .method(Method.POST)
                    .sseListener(new SseListener() {
                        @Override
                        public void event(SseReqSpec req, SseEvent msg) {
                        }
                    })
                    .<Object>okAsync()
                    .get(2, TimeUnit.SECONDS);
            Assertions.assertNotNull(value);
        } catch (ExecutionException ex) {
            Assertions.assertFalse(ex.getCause() instanceof ClassCastException);
            Assertions.assertTrue(ex.getCause().getMessage().contains("GET"));
        }
    }
}
