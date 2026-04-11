package io.github.kongweiguang.core.retry;

/**
 * 单次执行后的重试上下文。
 *
 * @param value       当前执行结果
 * @param error       当前执行异常
 * @param attempt     当前尝试次数，从 1 开始
 * @param maxAttempts 最大尝试次数
 * @author kongweiguang
 */
public record RetryContext<T>(T value, Throwable error, long attempt, long maxAttempts) {

    /**
     * 是否还有剩余重试次数。
     */
    public boolean hasRemainingAttempts() {
        return attempt < maxAttempts;
    }
}
