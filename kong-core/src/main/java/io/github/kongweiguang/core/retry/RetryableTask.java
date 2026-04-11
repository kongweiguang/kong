package io.github.kongweiguang.core.retry;


import io.github.kongweiguang.core.lang.Assert;
import io.github.kongweiguang.core.lang.Opt;
import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.core.threads.Threads;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

/**
 * 重试任务类
 *
 * @param <T> 任务结果类型
 * @author kongweiguang
 */
public class RetryableTask<T> {

    // region ----- retryFor

    /**
     * 重试根据指定的异常，没有返回值
     *
     * @param <T> 返回值类型
     * @param run 执行的方法 {@link Runnable}
     * @param ths 指定异常 {@link Throwable}，匹配任意一个异常时重试
     * @return 当前对象
     */
    @SafeVarargs
    public static <T> RetryableTask<T> retryForExceptions(Runnable run, Class<? extends Throwable>... ths) {
        return retryForExceptions(() -> {
            run.run();
            return null;
        }, ths);
    }

    /**
     * 重试根据指定的异常，有返回值
     *
     * @param <T> 返回值类型
     * @param sup 执行的方法 {@link Supplier}
     * @param ths 指定异常 {@link Throwable}，匹配任意一个异常时重试
     * @return 当前对象
     */
    @SafeVarargs
    public static <T> RetryableTask<T> retryForExceptions(Supplier<T> sup, Class<? extends Throwable>... ths) {
        Assert.isTrue(ths.length != 0, "exs cannot be empty");

        RetryPre<T> strategy = (t, e) -> {
            if (nonNull(e)) {
                boolean bool = Arrays.stream(ths).anyMatch(ex -> ex.isAssignableFrom(e.getClass()));
                return Pair.of(bool, t);
            }
            return Pair.of(false, t);
        };

        return new RetryableTask<>(sup, strategy);
    }

    /**
     * 重试根据指定的策略，没有返回值
     *
     * @param <T>       返回值类型
     * @param run       执行的方法 {@link Runnable}
     * @param predicate 策略 {@link BiPredicate}，返回{@code true}时表示重试
     * @return 当前对象
     */
    public static <T> RetryableTask<T> retryForPredicate(Runnable run, RetryPre<T> predicate) {
        return retryForPredicate(() -> {
            run.run();
            return null;
        }, predicate);
    }

    /**
     * 重试根据指定的策略，没有返回值
     *
     * @param <T>       返回值类型
     * @param sup       执行的方法 {@link  Supplier}
     * @param predicate 策略 {@link BiPredicate}，返回{@code true}时表示重试
     * @return 当前对象
     */
    public static <T> RetryableTask<T> retryForPredicate(Supplier<T> sup, RetryPre<T> predicate) {
        return new RetryableTask<>(sup, predicate);
    }
    // endregion

    /**
     * 执行结果
     */
    private T result;
    /**
     * 执行法方法
     */
    private Supplier<T> sup;
    /**
     * 重试策略
     */
    private RetryPre<T> predicate;
    /**
     * 重试次数，默认3次
     */
    private long maxAttempts = 3;
    /**
     * 重试间隔，默认1秒
     */
    private Duration delay = Duration.ofSeconds(1);
    /**
     * 异常信息
     */
    private Throwable throwable;

    /**
     * 构造方法，内部使用，调用请使用请用ofXXX
     *
     * @param sup       执行的方法
     * @param predicate 策略 {@link BiPredicate}，返回{@code true}时表示重试
     */
    private RetryableTask(Supplier<T> sup, RetryPre<T> predicate) {
        Assert.notNull(sup, "task parameter cannot be null");
        Assert.notNull(predicate, "predicate parameter cannot be null");

        this.predicate = predicate;
        this.sup = sup;
    }

    /**
     * 具体任务
     *
     * @param sup 执行的方法 {@link Supplier}
     * @return 当前对象
     */
    public RetryableTask<T> task(Supplier<T> sup) {
        Assert.notNull(sup, "task parameter cannot be null");

        this.sup = sup;
        return this;
    }

    /**
     * 重试策略
     *
     * @param predicate 策略 {@link BiPredicate}，返回{@code true}时表示重试
     * @return 当前对象
     */
    public RetryableTask<T> predicate(RetryPre<T> predicate) {
        Assert.notNull(predicate, "predicate parameter cannot be null");

        this.predicate = predicate;
        return this;
    }

    /**
     * 最大重试次数
     *
     * @param maxAttempts 次数
     * @return 当前对象
     */
    public RetryableTask<T> maxAttempts(long maxAttempts) {
        Assert.isTrue(maxAttempts > 0, "maxAttempts must be greater than 0");

        this.maxAttempts = maxAttempts;
        return this;
    }

    /**
     * 重试间隔时间
     *
     * @param delay 间隔时间
     * @return 当前对象
     */
    public RetryableTask<T> delay(Duration delay) {
        Assert.notNull(delay, "delay parameter cannot be null");

        this.delay = delay;
        return this;
    }

    /**
     * 获取结果
     *
     * @return 返回包装了结果的 {@link Opt}对象
     */
    public Opt<T, Throwable> get() {
        if (nonNull(result)) {
            return Opt.ofNullable(result);
        }

        if (nonNull(throwable)) {
            return Opt.error(throwable);
        }

        return Opt.empty();
    }


    /**
     * 异步执行重试方法
     *
     * @return 返回一个异步对象 {@link CompletableFuture}
     */
    public CompletableFuture<RetryableTask<T>> asyncExecute() {
        return CompletableFuture.supplyAsync(this::doExecute, Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * 同步执行重试方法
     *
     * @return 当前对象
     */
    public RetryableTask<T> execute() {
        return doExecute();
    }

    /**
     * 开始重试
     *
     * @return 当前对象
     **/
    private RetryableTask<T> doExecute() {
        Throwable th = null;

        while (--this.maxAttempts >= 0) {
            try {
                this.result = sup.get();
            } catch (Throwable t) {
                th = t;
            }

            //判断重试
            Pair<Boolean, T> test = predicate.test(result, th);
            if (!test.k()) {
                this.result = test.v();
                // 条件不满足时，跳出
                break;
            }

            Threads.sleep(delay.toMillis());
        }

        this.throwable = th;
        return this;
    }

}

