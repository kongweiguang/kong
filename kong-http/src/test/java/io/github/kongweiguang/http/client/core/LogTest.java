package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class LogTest {
    @Test
    public void test() throws Exception {
        Req.get("http://localhost:8080/get/one/two")

                .log(ReqLog.console, HttpLoggingInterceptor.Level.BODY)
                .timeout(Duration.ofMillis(1000))
                .ok()
                .then(r -> {
                    System.out.println(r.code());
                    System.out.println("ok -> " + r.isOk());
                })
                .then(r -> {
                    System.out.println("redirect -> " + r.isRedirect());
                });
    }
}
