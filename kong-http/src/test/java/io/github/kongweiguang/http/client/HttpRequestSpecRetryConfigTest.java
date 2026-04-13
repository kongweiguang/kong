package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.retry.HttpRetryConfig;
import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpRequestSpecRetryConfigTest {

    @Test
    void retryConsumerUpdatesHttpRetryConfig() {
        HttpReqSpec spec = Req.get("http://localhost")
                .retry(retry -> retry.maxAttempts(4)
                        .delay(Duration.ZERO)
                        .retryOnResponse(res -> res.code() == 429));

        HttpRetryConfig retry = spec.retry();
        assertEquals(4, retry.maxAttempts());
        assertEquals(Duration.ZERO, retry.delay());
        assertNotNull(retry.responsePredicate());
    }
}
