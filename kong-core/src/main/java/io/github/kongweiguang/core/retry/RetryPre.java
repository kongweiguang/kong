package io.github.kongweiguang.core.retry;

import io.github.kongweiguang.core.lang.Pair;

/**
 * 重试断言
 *
 * @param <R> 返回值
 * @author kongweiguang
 */
@FunctionalInterface
public interface RetryPre<R> {

    Pair<Boolean, R> test(R r, Throwable u);
}
