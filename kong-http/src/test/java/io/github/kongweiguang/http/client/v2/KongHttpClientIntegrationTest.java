package io.github.kongweiguang.http.client.v2;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.client.KongHttpClient;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Method;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class KongHttpClientIntegrationTest {

    @Test
    void shouldExecuteHttpAndInvokeSuccessHandler() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));
            server.start();

            CountDownLatch done = new CountDownLatch(1);
            AtomicReference<Integer> status = new AtomicReference<>();

            HttpRequestSpec spec = Req.of(server.url("/hello").toString())
                    .method(Method.POST)
                    .contentType(ContentType.JSON.v())
                    .body("{}")
                    .success(res -> {
                        status.set(res.code());
                        done.countDown();
                    })
                    .build();

            Res res = KongHttpClient.executeBlocking(spec);

            Assertions.assertEquals(200, res.code());
            Assertions.assertTrue(done.await(2, TimeUnit.SECONDS));
            Assertions.assertEquals(200, status.get());
        }
    }

    @Test
    void shouldSurfaceFailureAndInvokeFailureHandler() {
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        HttpRequestSpec spec = Req.get("http://127.0.0.1:1/unreachable")
                .fail(errorRef::set)
                .build();

        Assertions.assertThrows(RuntimeException.class, () -> KongHttpClient.executeBlocking(spec));
        Assertions.assertNotNull(errorRef.get());
    }
}
