package io.github.kongweiguang.core.util;


import io.github.kongweiguang.core.lang.Assert;
import io.github.kongweiguang.core.retry.RetryableTask;

import java.time.Duration;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * 重试工具类
 * 自定义功能请使用{@link RetryableTask}类
 *
 * @author kongweiguang
 * @see RetryableTask
 */
public class Retrys {

    /**
     * 根据异常信息进行重试
     * 没有返回值，重试执行方法
     *
     * @param run         执行方法
     * @param maxAttempts 最大的重试次数
     * @param delay       重试间隔
     * @param recover     达到最大重试次数后执行的备用方法
     * @param exs         指定的异常类型需要重试
     */
    public static void ofException(final Runnable run, final long maxAttempts, final Duration delay,
                                   final Runnable recover, Class<? extends Throwable>... exs) {
        Assert.isTrue(exs.length > 0, "exs cannot be empty");
        RetryableTask.retryForExceptions(run, exs)
                .maxAttempts(maxAttempts)
                .delay(delay)
                .execute()
                .get()
                .orElseGet(() -> {
                    recover.run();
                    return null;
                });
    }

    /**
     * 根据异常信息进行重试
     * 有返回值，重试执行方法
     *
     * @param sup         执行方法
     * @param maxAttempts 最大的重试次数
     * @param delay       重试间隔
     * @param recover     达到最大重试次数后执行的备用方法
     * @param exs         指定的异常类型需要重试
     * @param <T>         结果类型
     * @return 执行结果
     */
    public static <T> T ofException(final Supplier<T> sup, final long maxAttempts, final Duration delay,
                                    final Supplier<T> recover, Class<? extends Throwable>... exs) {
        Assert.isTrue(exs.length > 0, "exs cannot be empty");

        return RetryableTask.retryForExceptions(sup, exs)
                .maxAttempts(maxAttempts)
                .delay(delay)
                .execute()
                .get()
                .orElseGet(recover);
    }

    /**
     * 根据自定义结果进行重试
     * 没有返回值，重试执行方法
     *
     * @param run         执行方法
     * @param maxAttempts 最大的重试次数
     * @param delay       重试间隔
     * @param recover     达到最大重试次数后执行的备用方法
     * @param predicate   自定义重试条件
     */
    public static void ofPredicate(final Runnable run, final long maxAttempts, final Duration delay,
                                   final Supplier<Void> recover, final BiPredicate<Void, Throwable> predicate) {
        RetryableTask.retryForPredicate(run, predicate)
                .delay(delay)
                .maxAttempts(maxAttempts)
                .execute()
                .get()
                .orElseGet(recover);
    }


    /**
     * 根据异常信息进行重试
     * 有返回值，重试执行方法
     *
     * @param sup         执行方法
     * @param maxAttempts 最大的重试次数
     * @param delay       重试间隔
     * @param recover     达到最大重试次数后执行的备用方法
     * @param predicate   自定义重试条件
     * @param <T>         结果类型
     * @return 执行结果
     */
    public static <T> T ofPredicate(final Supplier<T> sup, final long maxAttempts, final Duration delay,
                                    final Supplier<T> recover, final BiPredicate<T, Throwable> predicate) {
        return RetryableTask.retryForPredicate(sup, predicate)
                .delay(delay)
                .maxAttempts(maxAttempts)
                .execute()
                .get()
                .orElseGet(recover);
    }

}
