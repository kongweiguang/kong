package io.github.kongweiguang.http.client.v2;

import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.core.retry.RetryableTask;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.v2.retry.HttpRetryPolicy;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

class HttpRetryPolicyTest {

    @Test
    void shouldRetryOnException() throws Exception {
        RetryableTask<Res> retryTask = RetryableTask.<Res>retryForPredicate(() -> null,
                        (res, throwable) -> Pair.of(throwable != null, res))
                .maxAttempts(3);

        HttpRetryPolicy policy = new HttpRetryPolicy(retryTask);
        AtomicInteger attempts = new AtomicInteger();

        Res result = policy.execute(() -> {
            if (attempts.incrementAndGet() < 3) {
                throw new IOException("fail");
            }
            return okRes();
        });

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, attempts.get());
    }

    private Res okRes() {
        Response response = new Response.Builder()
                .code(200)
                .message("ok")
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .body(ResponseBody.create(null, "ok"))
                .build();
        return Res.of(response);
    }
}
