package io.github.kongweiguang.core.retry;

import io.github.kongweiguang.core.lang.Assert;

import java.time.Duration;

/**
 * 重试间隔策略。
 *
 * @author kongweiguang
 */
@FunctionalInterface
public interface IntervalStrategy {

    /**
     * 返回下一次重试前的等待时长。
     */
    Duration nextDelay(RetryContext<?> context);

    /**
     * 固定间隔策略。
     */
    static IntervalStrategy fixed(Duration delay) {
        Assert.notNull(delay, "delay parameter cannot be null");
        Assert.isTrue(!delay.isNegative(), "delay must not be negative");
        return context -> delay;
    }
}
