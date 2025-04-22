package io.github.kongweiguang.core.test;

import java.time.Duration;

/**
 * 测试工具类
 *
 * @author kongweiguang
 */
public class Tests {
    /**
     * 创建限定时间的并发测试器
     *
     * @param threadNum 线程数
     * @param duration  持续时间
     * @return 测试解雇
     */
    public static TestResult test(int threadNum, Duration duration, Runnable run) {
        return LimitDateConcurrentTester.of(threadNum, duration).ok(run);
    }

    /**
     * 创建固定线程数的并发测试器
     *
     * @param threadNum 线程数
     * @param run       测试任务
     * @return 测试结果
     */
    public static TestResult test(int threadNum, Runnable run) {
        return FixedThreadConcurrentTester.of(threadNum).ok(run);
    }
}
