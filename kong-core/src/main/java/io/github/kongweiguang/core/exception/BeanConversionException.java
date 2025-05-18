package io.github.kongweiguang.core.exception;

/**
 * Bean转换异常
 *
 * @author kongweiguang
 */
public class BeanConversionException extends RuntimeException {
    public BeanConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}