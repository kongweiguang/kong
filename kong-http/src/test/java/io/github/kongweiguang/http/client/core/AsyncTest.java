package io.github.kongweiguang.http.client.core;


import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AsyncTest {

    @Test
    void test1() throws Exception {
        final CompletableFuture<Res> future = Req.get("http://localhost:8080/get")
                .query("a", "1")
                .success(r -> System.out.println("success : " + r.str()))
                .fail(System.out::println)
                .okAsync();

        System.out.println("res = " + future.get(3, TimeUnit.SECONDS));
    }

}
