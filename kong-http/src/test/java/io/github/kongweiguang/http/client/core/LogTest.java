package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class LogTest {
    @Test
    public void test() throws Exception {
        Res res = Req.get(TestHttpServer.url("/get/one/two"))
                .log(ReqLog.console, HttpLoggingInterceptor.Level.BODY)
                .timeout(Duration.ofMillis(1000))
                .ok();

        Assertions.assertEquals(200, res.code());
        Assertions.assertTrue(res.isOk());
    }
}
