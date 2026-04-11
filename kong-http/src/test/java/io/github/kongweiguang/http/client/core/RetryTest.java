package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class RetryTest {

    @Test
    public void testRetry() {
        TestHttpServer.resetRetry();
        Res res = Req.get(TestHttpServer.url("/retry"))
                .retry(retry -> retry.maxAttempts(3)
                        .delay(Duration.ofMillis(10)))
                .ok();

        Assertions.assertEquals(200, res.code());
        Assertions.assertEquals("success-after-retry", res.str());
        Assertions.assertEquals(3, TestHttpServer.retryAttempts());
    }

    @Test
    public void testRetry2() {
        TestHttpServer.resetRetryBody();
        Res res = Req.get(TestHttpServer.url("/retry-body"))
                .retry(retry -> retry.maxAttempts(3)
                        .delay(Duration.ofMillis(10))
                        .predicate((r, t) -> {
                            String str = r.str();
                            if (str.length() > 10) {
                                return Pair.of(false, r);
                            }
                            return Pair.of(true, r);
                        }))
                .ok();

        Assertions.assertEquals(200, res.code());
        Assertions.assertEquals(3, TestHttpServer.retryBodyAttempts());
    }

    @Test
    public void testRetry3() {
        TestHttpServer.resetRetry();
        CompletableFuture<Res> res = Req.get(TestHttpServer.url("/retry"))
                .retry(r -> r.maxAttempts(3).delay(Duration.ofMillis(10)))
                .okAsync();

        Res value = res.join();
        Assertions.assertEquals(200, value.code());
        Assertions.assertEquals("success-after-retry", value.str());
        Assertions.assertEquals(3, TestHttpServer.retryAttempts());
    }
}
