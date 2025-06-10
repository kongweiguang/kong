package io.github.kongweiguang.db.execption;

/**
 * 数据库异常
 *
 * @author kongweiguang
 */
public class KongSqlException extends RuntimeException {

    public KongSqlException(String message) {
        super(message);
    }

    public KongSqlException(Throwable cause) {
        super(cause);
    }
}
