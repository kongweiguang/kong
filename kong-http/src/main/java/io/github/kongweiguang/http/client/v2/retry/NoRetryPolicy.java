package io.github.kongweiguang.http.client.v2.retry;

import java.util.concurrent.Callable;

/**
 * Executes once without retry.
 */
public final class NoRetryPolicy<R> implements RetryPolicy<R> {
    @Override
    public R execute(Callable<R> task) throws Exception {
        return task.call();
    }
}

