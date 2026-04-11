package io.github.kongweiguang.http.client.v2.retry;

import java.util.concurrent.Callable;

/**
 * Retry abstraction to decouple execution from retry behavior.
 */
public interface RetryPolicy<R> {
    R execute(Callable<R> task) throws Exception;
}

