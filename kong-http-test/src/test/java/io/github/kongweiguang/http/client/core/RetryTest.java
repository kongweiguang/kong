package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class RetryTest {

    @Test
    public void testRetry() {
        Res res = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .retry(retry -> retry.maxAttempts(3)
                        .delay(Duration.ofSeconds(2)))
                .ok();
        System.out.println("res = " + res.str());
    }

    @Test
    public void testRetry2() {
        Res res = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .retry(retry -> retry.maxAttempts(3)
                        .delay(Duration.ofSeconds(2)))
                .ok();
        System.out.println("res.str() = " + res.str());
    }

    @Test
    public void testRetry3() {
        //异步重试
        CompletableFuture<Res> res = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .retry(r -> r.maxAttempts(3))
                .okAsync();
        System.out.println("res.join().str() = " + res.join().str());
    }
}
