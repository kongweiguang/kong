package io.github.kongweiguang.core.test;

import io.github.kongweiguang.core.threads.ThreadPools;

import java.util.concurrent.CompletableFuture;

/**
 * 固定线程数的并发测试器
 *
 * @author kongweiguang
 */
public class FixedThreadConcurrentTester extends ConcurrentTester {
    public FixedThreadConcurrentTester(int threadNum, long durationMillis) {
        super(threadNum, durationMillis);
    }

    /**
     * 创建固定线程数的并发测试器
     *
     * @param threadNum 线程数
     * @return 并发测试器
     */
    public static FixedThreadConcurrentTester of(int threadNum) {
        return new FixedThreadConcurrentTester(threadNum, -1);
    }


    @Override
    Tester ok(Runnable task) {
        CompletableFuture<?>[] futures = new CompletableFuture[threadNum];

        // 初始化所有任务
        for (int i = 0; i < threadNum; i++) {
            futures[i] = CompletableFuture.runAsync(() -> executeTask(task), ThreadPools.pool);
        }

        // 等待所有任务完成或超时
        try {
            CompletableFuture.allOf(futures).join();
        } catch (Exception e) {
            System.out.println("测试已超时，自动终止...");
        }

        return buildTester();
    }
}
