package io.github.kongweiguang.db.sql;

import java.util.Arrays;
import java.util.StringJoiner;

public class SqlRes {
    private final String sql;
    private final Object[] params;

    public SqlRes(String sql, Object[] params) {
        this.sql = sql;
        this.params = params;
    }

    public String sql() {
        return sql;
    }

    public Object[] params() {
        return params;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SqlRes.class.getSimpleName() + "[", "]")
                .add("sql='" + sql + "'")
                .add("params=" + Arrays.toString(params))
                .toString();
    }
}