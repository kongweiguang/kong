package io.github.kongweiguang.http.common.exception;

/**
 * 运行时异常
 *
 * @author kongweiguang
 */
public class KongHttpRuntimeException extends RuntimeException {
    /**
     * 创建KongHttpRuntimeException 实例。
     */
    public KongHttpRuntimeException(String message) {
        super(message);
    }

    /**
     * 创建KongHttpRuntimeException 实例。
     */
    public KongHttpRuntimeException(Throwable cause) {
        super(cause);
    }
}
