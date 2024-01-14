package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class RetryTest {

    @Test
    void testRetry() {
        final Res res = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .retry(3)
                .ok();
        System.out.println("res = " + res.str());
    }

    @Test
    void testRetry2() {
        final Res res = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .retry(3, Duration.ofSeconds(2), (r, t) -> {
                    final String str = r.str();
                    if (str.length() > 10) {
                        return true;
                    }
                    return false;
                })
                .ok();
        System.out.println("res.str() = " + res.str());
    }

    @Test
    void testRetry3() {
        //异步重试
        final CompletableFuture<Res> res = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .retry(3)
                .okAsync();
        System.out.println(1);
        System.out.println("res.join().str() = " + res.join().str());
    }
}
