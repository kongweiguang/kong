package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Method;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class KongHttpClientIntegrationTest {

    @Test
    void shouldExecuteHttpViaBuilderOkAndInvokeSuccessHandler() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));
            server.start();

            CountDownLatch done = new CountDownLatch(1);
            AtomicReference<Integer> status = new AtomicReference<>();

            Res res = Req.of(server.url("/hello").toString())
                    .method(Method.POST)
                    .contentType(ContentType.JSON.value())
                    .json("{}")
                    .success(successRes -> {
                        status.set(successRes.code());
                        done.countDown();
                    })
                    .ok();

            Assertions.assertEquals(200, res.code());
            Assertions.assertTrue(done.await(2, TimeUnit.SECONDS));
            Assertions.assertEquals(200, status.get());
        }
    }

    @Test
    void shouldExecuteHttpViaBuilderOkAsync() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(200).setBody("async-ok"));
            server.start();

            CompletableFuture<Res> future = Req.get(server.url("/async").toString()).okAsync();
            Res res = future.get(2, TimeUnit.SECONDS);
            Assertions.assertEquals(200, res.code());
            Assertions.assertEquals("async-ok", res.str());
        }
    }

    @Test
    void shouldSurfaceFailureAndInvokeFailureHandlerViaBuilderOk() {
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        Assertions.assertThrows(RuntimeException.class, () -> Req.get("http://127.0.0.1:1/unreachable")
                .fail(errorRef::set)
                .ok());
        Assertions.assertNotNull(errorRef.get());
    }

    @Test
    void shouldSurfaceFailureAndInvokeFailureHandlerViaBuilderOkAsync() {
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        CompletableFuture<Res> future = Req.get("http://127.0.0.1:1/unreachable")
                .fail(errorRef::set)
                .okAsync();

        Assertions.assertThrows(Exception.class, future::get);
        Assertions.assertNotNull(errorRef.get());
    }


}
