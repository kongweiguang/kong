package io.github.kongweiguang.db.func;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 结果集处理函数
 *
 * @author kongweiguang
 */
@FunctionalInterface
public interface RsFn<T> {
    T handle(ResultSet rs) throws SQLException;
}