package io.github.kongweiguang.http.client.retry;

import io.github.kongweiguang.http.client.Res;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpRetryExecutorTest {

    @Test
    void shouldRetryOnException() throws Exception {
        HttpRetryConfig retry = new HttpRetryConfig()
                .maxAttempts(3)
                .delay(Duration.ZERO);

        HttpRetryExecutor executor = new HttpRetryExecutor(retry);
        AtomicInteger attempts = new AtomicInteger();

        Res result = executor.execute(() -> {
            if (attempts.incrementAndGet() < 3) {
                throw new IOException("fail");
            }
            return okRes();
        });

        assertNotNull(result);
        assertEquals(3, attempts.get());
    }

    @Test
    void shouldRetryOnResponsePredicate() throws Exception {
        HttpRetryConfig retry = new HttpRetryConfig()
                .maxAttempts(3)
                .delay(Duration.ZERO)
                .retryOnResponse(res -> res.code() == 503);

        HttpRetryExecutor executor = new HttpRetryExecutor(retry);
        AtomicInteger attempts = new AtomicInteger();

        Res result = executor.execute(() -> attempts.incrementAndGet() < 3 ? errorRes() : okRes());

        assertEquals(200, result.code());
        assertEquals(3, attempts.get());
    }

    private Res okRes() {
        return responseOf(200, "ok");
    }

    private Res errorRes() {
        return responseOf(503, "busy");
    }

    private Res responseOf(int code, String body) {
        Response response = new Response.Builder()
                .code(code)
                .message(body)
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .body(ResponseBody.create(null, body))
                .build();
        return Res.of(response);
    }
}
