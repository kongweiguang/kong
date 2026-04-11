package io.github.kongweiguang.http.client.retry;

import java.util.concurrent.Callable;

/**
 * 只执行一次，不进行重试。
 */
public final class NoRetryPolicy<R> implements RetryPolicy<R> {
    @Override
    public R execute(Callable<R> task) throws Exception {
        return task.call();
    }
}


