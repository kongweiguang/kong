package io.github.kongweiguang.core.retry;

import io.github.kongweiguang.core.lang.Assert;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 不可变的重试策略。
 *
 * @author kongweiguang
 */
public final class RetryPolicy<T> {

    private final long maxAttempts;
    private final IntervalStrategy intervalStrategy;
    private final Predicate<RetryContext<T>> retryPredicate;
    private final Function<RetryContext<T>, T> recovery;

    private RetryPolicy(Builder<T> builder) {
        this.maxAttempts = builder.maxAttempts;
        this.intervalStrategy = builder.intervalStrategy;
        this.retryPredicate = builder.retryPredicate;
        this.recovery = builder.recovery;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public long maxAttempts() {
        return maxAttempts;
    }

    public IntervalStrategy intervalStrategy() {
        return intervalStrategy;
    }

    public Predicate<RetryContext<T>> retryPredicate() {
        return retryPredicate;
    }

    public Function<RetryContext<T>, T> recovery() {
        return recovery;
    }

    public static final class Builder<T> {

        private long maxAttempts = 3;
        private IntervalStrategy intervalStrategy = IntervalStrategy.fixed(Duration.ofSeconds(1));
        private Predicate<RetryContext<T>> retryPredicate = context -> false;
        private Function<RetryContext<T>, T> recovery;

        private Builder() {
        }

        public Builder<T> maxAttempts(long maxAttempts) {
            Assert.isTrue(maxAttempts > 0, "maxAttempts must be greater than 0");
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder<T> interval(Duration delay) {
            return interval(IntervalStrategy.fixed(delay));
        }

        public Builder<T> interval(IntervalStrategy intervalStrategy) {
            this.intervalStrategy = Objects.requireNonNull(intervalStrategy, "intervalStrategy");
            return this;
        }

        public Builder<T> retryIf(Predicate<RetryContext<T>> retryPredicate) {
            this.retryPredicate = Objects.requireNonNull(retryPredicate, "retryPredicate");
            return this;
        }

        public Builder<T> recover(Function<RetryContext<T>, T> recovery) {
            this.recovery = Objects.requireNonNull(recovery, "recovery");
            return this;
        }

        public RetryPolicy<T> build() {
            return new RetryPolicy<>(this);
        }
    }
}
