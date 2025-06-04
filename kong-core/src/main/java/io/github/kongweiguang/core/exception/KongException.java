package io.github.kongweiguang.core.exception;

/**
 * 自定义异常
 *
 * @author kongweiguang
 */
public class KongException extends RuntimeException {
    public KongException(String message, Throwable cause) {
        super(message, cause);
    }

    public KongException(Throwable cause) {
        super(cause);
    }
}
