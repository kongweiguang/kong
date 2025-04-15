package io.github.kongweiguang.core.test;

import io.github.kongweiguang.core.threads.ThreadPools;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 限定时间的并发测试器
 *
 * @author kongweiguang
 */
public class LimitDateConcurrentTester extends ConcurrentTester {

    private LimitDateConcurrentTester(int threadNum, long durationMillis) {
        super(threadNum, durationMillis);
    }

    /**
     * 创建限定时间的并发测试器
     *
     * @param threadNum      线程数
     * @param durationMillis 持续时间
     * @return 并发测试器
     */
    public static LimitDateConcurrentTester of(int threadNum, Duration durationMillis) {
        return new LimitDateConcurrentTester(threadNum, durationMillis.toMillis());
    }

    /**
     * 开始测试
     *
     * @param task 测试任务
     * @return 测试结果
     */
    public Tester ok(Runnable task) {
        long endTime = System.currentTimeMillis() + durationMillis;
        CompletableFuture<?>[] futures = new CompletableFuture[threadNum];

        // 初始化所有任务
        for (int i = 0; i < threadNum; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                while (System.currentTimeMillis() < endTime) {
                    executeTask(task);
                }
            }, ThreadPools.pool);
        }

        // 等待所有任务完成或超时
        try {
            CompletableFuture.allOf(futures).get(durationMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.out.println("测试已超时，自动终止...");
        }

        return buildTester();
    }


}