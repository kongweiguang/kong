package io.github.kongweiguang.core.retry;

/**
 * 重试执行结果。
 *
 * @param value     最终结果
 * @param error     最终异常
 * @param attempts  实际尝试次数
 * @param recovered 是否通过恢复器产出了结果
 * @author kongweiguang
 */
public record RetryResult<T>(T value, Throwable error, long attempts, boolean recovered) {

    /**
     * 是否成功返回结果。
     */
    public boolean isSuccess() {
        return error == null;
    }

    /**
     * 是否由恢复器生成结果。
     */
    public boolean isRecovered() {
        return recovered;
    }

    /**
     * 创建成功结果。
     */
    public static <T> RetryResult<T> success(T value, long attempts) {
        return new RetryResult<>(value, null, attempts, false);
    }

    /**
     * 创建恢复后的结果。
     */
    public static <T> RetryResult<T> recovered(T value, long attempts) {
        return new RetryResult<>(value, null, attempts, true);
    }

    /**
     * 创建失败结果。
     */
    public static <T> RetryResult<T> failure(Throwable error, long attempts) {
        return new RetryResult<>(null, error, attempts, false);
    }
}
