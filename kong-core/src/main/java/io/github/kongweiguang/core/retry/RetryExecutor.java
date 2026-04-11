package io.github.kongweiguang.core.retry;

import io.github.kongweiguang.core.lang.Assert;
import io.github.kongweiguang.core.threads.Threads;

/**
 * 重试执行器。
 *
 * @author kongweiguang
 */
public final class RetryExecutor {

    private RetryExecutor() {
    }

    /**
     * 执行并在最终失败时抛出异常。
     */
    public static <T> T execute(RetryPolicy<T> policy, CheckedSupplier<T> supplier) throws Exception {
        RetryResult<T> result = executeSafely(policy, supplier);
        if (result.isSuccess()) {
            return result.value();
        }

        Throwable error = result.error();
        if (error instanceof Exception exception) {
            throw exception;
        }
        if (error instanceof Error err) {
            throw err;
        }
        throw new RuntimeException(error);
    }

    /**
     * 执行并返回安全结果对象。
     */
    public static <T> RetryResult<T> executeSafely(RetryPolicy<T> policy, CheckedSupplier<T> supplier) {
        Assert.notNull(policy, "policy parameter cannot be null");
        Assert.notNull(supplier, "supplier parameter cannot be null");

        for (long attempt = 1; attempt <= policy.maxAttempts(); attempt++) {
            T value = null;
            Throwable error = null;
            try {
                value = supplier.get();
            } catch (Throwable throwable) {
                error = throwable;
            }

            RetryContext<T> context = new RetryContext<>(value, error, attempt, policy.maxAttempts());
            boolean shouldRetry = context.hasRemainingAttempts() && policy.retryPredicate().test(context);
            if (!shouldRetry) {
                if (error == null) {
                    return RetryResult.success(value, attempt);
                }
                if (policy.recovery() != null) {
                    return RetryResult.recovered(policy.recovery().apply(context), attempt);
                }
                return RetryResult.failure(error, attempt);
            }

            long millis = policy.intervalStrategy().nextDelay(context).toMillis();
            if (millis > 0) {
                Threads.sleep(millis);
            }
        }

        throw new IllegalStateException("retry execution ended unexpectedly");
    }

    @FunctionalInterface
    public interface CheckedSupplier<T> {

        T get() throws Exception;
    }
}
