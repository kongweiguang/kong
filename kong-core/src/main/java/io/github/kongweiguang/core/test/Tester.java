package io.github.kongweiguang.core.test;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试结果
 *
 * @author kongweiguang
 */
public class Tester {
    // 总线程数
    private Integer totalThreads;
    // 持续时间
    private Long durationMillis;
    // 基本数据
    private BaseStatistics statistics;
    // 性能指标
    private PerformanceMetrics performanceMetrics;
    // 错误分析
    private ErrorAnalysis errorAnalysis;

    //基础统计
    public static class BaseStatistics {
        //总请求数
        private Integer totalCount;
        //成功请求数
        private Integer successCount;
        //失败请求数
        private Integer failureCount;

        public Integer totalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer successCount() {
            return successCount;
        }

        public void setSuccessCount(Integer successCount) {
            this.successCount = successCount;
        }

        public Integer failureCount() {
            return failureCount;
        }

        public void setFailureCount(Integer failureCount) {
            this.failureCount = failureCount;
        }
    }

    //性能指标
    public static class PerformanceMetrics {
        //总耗时（ms）
        private Long useTime;
        //平均耗时
        private Double avgTime;
        //最大耗时
        private Double maxTime;
        //最小耗时
        private Double minTime;
        //90%耗时
        private Double ninetyPercentTime;
        //95%耗时
        private Double ninetyFivePercentTime;
        //99%耗时
        private Double ninetyNinePercentTime;
        //qps（每秒执行次数）
        private Double qps;

        public Long useTime() {
            return useTime;
        }

        public void setUseTime(Long useTime) {
            this.useTime = useTime;
        }

        public Double avgTime() {
            return avgTime;
        }

        public void setAvgTime(Double avgTime) {
            this.avgTime = avgTime;
        }

        public Double maxTime() {
            return maxTime;
        }

        public void setMaxTime(Double maxTime) {
            this.maxTime = maxTime;
        }

        public Double minTime() {
            return minTime;
        }

        public void setMinTime(Double minTime) {
            this.minTime = minTime;
        }

        public Double ninetyPercentTime() {
            return ninetyPercentTime;
        }

        public void setNinetyPercentTime(Double ninetyPercentTime) {
            this.ninetyPercentTime = ninetyPercentTime;
        }

        public Double ninetyFivePercentTime() {
            return ninetyFivePercentTime;
        }

        public void setNinetyFivePercentTime(Double ninetyFivePercentTime) {
            this.ninetyFivePercentTime = ninetyFivePercentTime;
        }

        public Double ninetyNinePercentTime() {
            return ninetyNinePercentTime;
        }

        public void setNinetyNinePercentTime(Double ninetyNinePercentTime) {
            this.ninetyNinePercentTime = ninetyNinePercentTime;
        }

        public Double qps() {
            return qps;
        }

        public void setQps(Double qps) {
            this.qps = qps;
        }
    }

    //错误分析
    public static class ErrorAnalysis {
        private Map<String, AtomicInteger> exceptionCounter = new ConcurrentHashMap<>();

        public Map<String, AtomicInteger> exceptionCounter() {
            return exceptionCounter;
        }

        public void setExceptionCounter(Map<String, AtomicInteger> exceptionCounter) {
            this.exceptionCounter = exceptionCounter;
        }
    }

    public Integer totalThreads() {
        return totalThreads;
    }

    public void setTotalThreads(Integer totalThreads) {
        this.totalThreads = totalThreads;
    }

    public Long durationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public BaseStatistics statistics() {
        return statistics;
    }

    public void setStatistics(BaseStatistics statistics) {
        this.statistics = statistics;
    }

    public PerformanceMetrics performanceMetrics() {
        return performanceMetrics;
    }

    public void setPerformanceMetrics(PerformanceMetrics performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }

    public ErrorAnalysis errorAnalysis() {
        return errorAnalysis;
    }

    public void setErrorAnalysis(ErrorAnalysis errorAnalysis) {
        this.errorAnalysis = errorAnalysis;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();

        BaseStatistics statistics = statistics();
        PerformanceMetrics metrics = performanceMetrics();
        sb.append("====================================================================================================").append("\n");
        sb.append("并发测试报告").append("\n");
        sb.append("测试时间: ").append(new Date()).append("\n");
        sb.append("并发线程数: ").append(totalThreads()).append("\n");
        sb.append("测试持续时间: ").append(formatDuration(durationMillis())).append("\n");


        int total = statistics.totalCount();
        int success = statistics.successCount();
        int failed = statistics.failureCount();
        double successRate = total == 0 ? 0 : (double) success / total * 100;
        double errorRate = total == 0 ? 0 : (double) failed / total * 100;

        sb.append("--------------------------------------------------").append("\n");
        sb.append("基础统计").append("\n");
        sb.append("总请求数: ").append(total).append("\n");
        sb.append("成功请求数: ").append(success).append(" (").append(successRate).append("%)").append("\n");
        sb.append("失败请求数: ").append(failed).append(" (").append(errorRate).append("%)").append("\n");

        sb.append("--------------------------------------------------").append("\n");
        sb.append("性能指标").append("\n");
        sb.append("平均耗时: ").append(metrics.avgTime()).append(" ms").append("\n");
        sb.append("最大耗时: ").append(metrics.maxTime()).append(" ms").append("\n");
        sb.append("最小耗时: ").append(metrics.minTime()).append(" ms").append("\n");
        sb.append("90%% 响应时间: ").append(metrics.ninetyPercentTime()).append(" ms").append("\n");
        sb.append("95%% 响应时间: ").append(metrics.ninetyFivePercentTime()).append(" ms").append("\n");
        sb.append("99%% 响应时间: ").append(metrics.ninetyNinePercentTime()).append(" ms").append("\n");
        sb.append("QPS: ").append(metrics.qps()).append(" req/s").append("\n");

        sb.append("--------------------------------------------------").append("\n");
        sb.append("错误分析").append("\n");
        errorAnalysis().exceptionCounter().entrySet().stream()
                .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                .forEach(entry -> {
                    int count = entry.getValue().get();
                    sb.append("- ").append(entry.getKey()).append(" : ").append(count).append(" 次 (").append((double) count / statistics().totalCount() * 100).append("%)").append("\n");
                });

        sb.append("====================================================================================================").append("\n");
        return sb.toString();
    }

    private String formatDuration(long millis) {
        return String.format("%d分%d秒",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
    }

    @Override
    public String toString() {
        return print();
    }
}
