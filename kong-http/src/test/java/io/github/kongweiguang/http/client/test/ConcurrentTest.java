package io.github.kongweiguang.http.client.test;

import io.github.kongweiguang.core.test.TestResult;
import io.github.kongweiguang.core.test.Tests;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class ConcurrentTest {


    @Test
    public void test3() throws Exception {
        TestResult test = Tests.test(10, Duration.ofSeconds(30), () -> {
            Res res = Req.get("http://localhost:8888").ok();
            if (!res.isOk()) {

                throw new RuntimeException("<UNK>");
            }
        });

        System.out.println(test.print());
    }
}
