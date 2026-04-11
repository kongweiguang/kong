package io.github.kongweiguang.http.client.exception;

/**
 * 请求运行时异常
 *
 * @author kongweiguang
 */
public class KongHttpRuntimeException extends RuntimeException {

    public KongHttpRuntimeException(String message) {
        super(message);
    }

    public KongHttpRuntimeException(Throwable cause) {
        super(cause);
    }
}
