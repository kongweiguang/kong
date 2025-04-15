package io.github.kongweiguang.core.test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 并发测试器
 * @author kongweiguang
 */
public abstract class ConcurrentTester {

    // 统计数据
    protected final AtomicInteger totalRequests = new AtomicInteger(0);
    protected final AtomicInteger successRequests = new AtomicInteger(0);
    protected final AtomicInteger failedRequests = new AtomicInteger(0);
    protected final AtomicLong totalCost = new AtomicLong(0);
    protected final Map<String, AtomicInteger> exceptionCounter = new ConcurrentHashMap<>();
    protected final List<Long> sortedCosts = new CopyOnWriteArrayList<>();

    // 配置参数
    protected final int threadNum;
    protected final long durationMillis;

    protected ConcurrentTester(int threadNum, long durationMillis) {
        this.threadNum = threadNum;
        this.durationMillis = durationMillis;
    }

    /**
     * 开始测试
     *
     * @param task 测试任务
     * @return 测试结果
     */
    abstract Tester ok(Runnable task);

    protected Tester buildTester() {
        Tester tester = new Tester();
        tester.setTotalThreads(threadNum);
        tester.setDurationMillis(durationMillis);

        Tester.BaseStatistics statistics = new Tester.BaseStatistics();
        statistics.setTotalCount(totalRequests.get());
        statistics.setSuccessCount(successRequests.get());
        statistics.setFailureCount(failedRequests.get());
        tester.setStatistics(statistics);

        Tester.PerformanceMetrics metrics = new Tester.PerformanceMetrics();
        metrics.setUseTime(totalCost.get());
        metrics.setAvgTime(average(sortedCosts) / 1_000_000);
        metrics.setMaxTime(max(sortedCosts) / 1_000_000);
        metrics.setMinTime(min(sortedCosts) / 1_000_000);
        metrics.setNinetyPercentTime(percentile(sortedCosts, 90) / 1_000_000);
        metrics.setNinetyFivePercentTime(percentile(sortedCosts, 95) / 1_000_000);
        metrics.setNinetyNinePercentTime(percentile(sortedCosts, 99) / 1_000_000);
        metrics.setQps(qps(sortedCosts));
        tester.setPerformanceMetrics(metrics);

        Tester.ErrorAnalysis errorAnalysis = new Tester.ErrorAnalysis();
        errorAnalysis.setExceptionCounter(exceptionCounter);
        tester.setErrorAnalysis(errorAnalysis);
        return tester;
    }

    protected void executeTask(Runnable task) {
        long startTime = System.nanoTime();
        try {
            task.run();
            successRequests.incrementAndGet();
            updateMetrics(System.nanoTime() - startTime);
        } catch (Exception e) {
            failedRequests.incrementAndGet();
            exceptionCounter.computeIfAbsent(e.getClass().getSimpleName(), k -> new AtomicInteger()).incrementAndGet();
        } finally {
            totalRequests.incrementAndGet();
        }
    }

    protected synchronized void updateMetrics(long cost) {
        if (cost > 0) {
            totalCost.addAndGet(cost);
            sortedCosts.add(cost);
        }
    }

    protected double average(List<Long> list) {
        return list.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    protected double percentile(List<Long> list, double percentile) {
        int index = (int) Math.ceil(percentile / 100 * list.size()) - 1;
        return list.get(index);
    }

    protected double max(List<Long> list) {
        return list.stream().mapToLong(Long::longValue).max().orElse(0);
    }

    protected double min(List<Long> list) {
        return list.stream().mapToLong(Long::longValue).min().orElse(0);
    }

    protected double qps(List<Long> list) {
        if (list.isEmpty()) return 0;
        return list.size() * 1000.0 / (average(list) / 1_000_000);
    }

}
