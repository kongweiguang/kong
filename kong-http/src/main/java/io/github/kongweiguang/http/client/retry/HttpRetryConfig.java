package io.github.kongweiguang.http.client.retry;

import io.github.kongweiguang.core.lang.Assert;
import io.github.kongweiguang.http.client.Res;

import java.time.Duration;
import java.util.function.Predicate;

/**
 * HTTP 重试配置。
 *
 * @author kongweiguang
 */
public final class HttpRetryConfig {

    private long maxAttempts = 1;
    private Duration delay = Duration.ofSeconds(1);
    private Predicate<Throwable> exceptionPredicate = throwable -> true;
    private Predicate<Res> responsePredicate = res -> res != null && !res.isOk();

    /**
     * 返回 HTTP 请求的最大尝试次数，包含首次请求。
     */
    public long maxAttempts() {
        return maxAttempts;
    }

    /**
     * 返回两次重试之间的固定等待时间。
     */
    public Duration delay() {
        return delay;
    }

    /**
     * 返回异常重试判定逻辑。
     */
    public Predicate<Throwable> exceptionPredicate() {
        return exceptionPredicate;
    }

    /**
     * 返回响应重试判定逻辑。
     */
    public Predicate<Res> responsePredicate() {
        return responsePredicate;
    }

    /**
     * 设置最大尝试次数，包含首次请求。
     */
    public HttpRetryConfig maxAttempts(long maxAttempts) {
        Assert.isTrue(maxAttempts > 0, "maxAttempts must be greater than 0");
        this.maxAttempts = maxAttempts;
        return this;
    }

    /**
     * 设置固定重试间隔。
     */
    public HttpRetryConfig delay(Duration delay) {
        Assert.notNull(delay, "delay parameter cannot be null");
        Assert.isTrue(!delay.isNegative(), "delay must not be negative");
        this.delay = delay;
        return this;
    }

    /**
     * 设置异常重试条件。
     * 默认情况下，只要请求抛异常就允许重试。
     */
    public HttpRetryConfig retryOnException(Predicate<Throwable> exceptionPredicate) {
        Assert.notNull(exceptionPredicate, "exceptionPredicate parameter cannot be null");
        this.exceptionPredicate = exceptionPredicate;
        return this;
    }

    /**
     * 设置响应重试条件。
     * 默认情况下，对非成功响应进行重试。
     */
    public HttpRetryConfig retryOnResponse(Predicate<Res> responsePredicate) {
        Assert.notNull(responsePredicate, "responsePredicate parameter cannot be null");
        this.responsePredicate = responsePredicate;
        return this;
    }

}
