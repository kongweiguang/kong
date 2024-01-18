package io.github.kongweiguang.bus.test.async;

import io.github.kongweiguang.bus.Bus;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

public class AsyncTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        Bus.<String, String>hub().pull(branch, h -> CompletableFuture.runAsync(() -> {
            h.res("123");
            System.out.println(Thread.currentThread().getName());
            System.out.println(h);
        }));

        //推送消息
        Bus.<String, String>hub().push(branch, "content", r -> System.out.println(r));
    }
}
