package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.TestHttpServer;
import io.github.kongweiguang.http.common.exception.KongHttpRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionException;

public class TimeoutTest {

    @Test
    void test1() {
        CompletionException ex = Assertions.assertThrows(CompletionException.class, () ->
                Req.get(TestHttpServer.url("/timeout"))
                        .timeout(Duration.ofSeconds(1))
                        .ok()
        );
        Assertions.assertInstanceOf(KongHttpRuntimeException.class, ex.getCause());
    }

}
