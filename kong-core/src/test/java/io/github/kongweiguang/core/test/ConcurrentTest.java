package io.github.kongweiguang.core.test;

import org.junit.jupiter.api.Test;

import java.time.Duration;

public class ConcurrentTest {

    @Test
    public void test1() {
//        ThreadPools.pool = Executors.newVirtualThreadPerTaskExecutor();

        Tester tester = Tests.test(20, Duration.ofMinutes(1), () -> {
            try {
                // 模拟正常请求（80%概率）
                if (Math.random() < 0.8) {
                    Thread.sleep((long) (Math.random() * 500));
                } else {
                    // 模拟异常请求（20%概率）
                    throw new RuntimeException("模拟业务异常");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println(tester.print());
    }

    @Test
    public void test2() {
//        ThreadPools.pool = Executors.newVirtualThreadPerTaskExecutor();

        Tester tester = Tests.test(20, () -> {
            try {
                // 模拟正常请求（80%概率）
                if (Math.random() < 0.8) {
                    Thread.sleep((long) (Math.random() * 500));
                } else {
                    // 模拟异常请求（20%概率）
                    throw new RuntimeException("模拟业务异常");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println(tester.print());
    }
}
