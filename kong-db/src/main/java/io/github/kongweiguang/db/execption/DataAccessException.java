package io.github.kongweiguang.db.execption;

/**
 * 数据访问异常
 *
 * @author kongweiguang
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}