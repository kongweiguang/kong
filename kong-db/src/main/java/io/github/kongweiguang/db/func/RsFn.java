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
    T hd(ResultSet rs) throws SQLException;
}