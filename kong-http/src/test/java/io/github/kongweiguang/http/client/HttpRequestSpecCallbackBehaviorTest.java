package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpRequestSpecCallbackBehaviorTest {

    @Test
    void okShouldReturnResponseWithoutInvokingSuccessCallback() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(200).setBody("sync-ok"));
            server.start();

            AtomicReference<Res> callbackRes = new AtomicReference<>();

            Res res = Req.get(server.url("/sync").toString())
                    .success(callbackRes::set)
                    .ok();

            assertEquals(200, res.code());
            assertEquals("sync-ok", res.str());
            assertNull(callbackRes.get());
        }
    }

    @Test
    void okShouldThrowWithoutInvokingFailureCallback() {
        AtomicReference<Throwable> callbackError = new AtomicReference<>();

        assertThrows(KongHttpRuntimeException.class, () -> Req.get("http://127.0.0.1:1/unreachable")
                .fail(callbackError::set)
                .ok());

        assertNull(callbackError.get());
    }

    @Test
    void okAsyncShouldInvokeSuccessCallbackAndReturnResponse() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(200).setBody("async-ok"));
            server.start();

            AtomicReference<Res> callbackRes = new AtomicReference<>();

            CompletableFuture<Res> future = Req.get(server.url("/async").toString())
                    .success(callbackRes::set)
                    .okAsync();

            Res res = future.get(2, TimeUnit.SECONDS);
            assertEquals(200, res.code());
            assertNotNull(callbackRes.get());
            assertEquals(200, callbackRes.get().code());
        }
    }

    @Test
    void okAsyncShouldInvokeFailureCallbackAndCompleteExceptionally() {
        AtomicReference<Throwable> callbackError = new AtomicReference<>();

        CompletableFuture<Res> future = Req.get("http://127.0.0.1:1/unreachable")
                .fail(callbackError::set)
                .okAsync();

        assertThrows(ExecutionException.class, future::get);
        assertNotNull(callbackError.get());
    }
}
