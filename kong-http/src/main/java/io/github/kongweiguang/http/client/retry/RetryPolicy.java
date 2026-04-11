package io.github.kongweiguang.http.client.retry;

import java.util.concurrent.Callable;

/**
 * 重试抽象，用于解耦执行逻辑与重试策略。
 */
public interface RetryPolicy<R> {
    R execute(Callable<R> task) throws Exception;
}


