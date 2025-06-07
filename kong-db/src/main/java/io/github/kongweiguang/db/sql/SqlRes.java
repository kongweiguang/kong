package io.github.kongweiguang.db.sql;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * SQL执行结果
 *
 * @author kongweiguang
 */
public record SqlRes(String sql, Object[] params) {

    @Override
    public String toString() {
        return new StringJoiner(", ", SqlRes.class.getSimpleName() + "[", "]")
                .add("sql='" + sql + "'")
                .add("params=" + Arrays.toString(params))
                .toString();
    }

}