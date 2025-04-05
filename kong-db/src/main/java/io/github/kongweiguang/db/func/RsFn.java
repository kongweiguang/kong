package io.github.kongweiguang.db.func;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RsFn<T> {
    T hd(ResultSet rs) throws SQLException;
}