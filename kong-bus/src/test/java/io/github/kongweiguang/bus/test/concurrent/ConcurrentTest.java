package io.github.kongweiguang.bus.test.concurrent;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static io.github.kongweiguang.bus.Bus.hub;

public class ConcurrentTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        AtomicLong at = new AtomicLong();
        //拉取消息
        hub().pull(branch, h -> at.incrementAndGet());

        final long start = System.currentTimeMillis();
        for (int i = 0; i < 100_000_000; i++) {
            //推送消息
            hub().push(branch, "content");
        }
        final long end = System.currentTimeMillis();
        System.out.println("use time -> " + (end - start) + "ms");
        System.out.println("at = " + at.get());
        //一亿用时，1235ms
    }
}
