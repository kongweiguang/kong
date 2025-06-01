package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class TimeoutTest {

    @Test
    void test1() throws Exception {
        Res res = Req.get("http://localhost:8080/timeout")
                .timeout(Duration.ofSeconds(1))
//        .timeout(10, 10, 10)
                .ok();
        System.out.println(res.str());
    }

}
