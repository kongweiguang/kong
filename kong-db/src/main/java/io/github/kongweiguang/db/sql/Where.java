package io.github.kongweiguang.db.sql;


import io.github.kongweiguang.core.lang.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * 条件对象
 *
 * @author kongweiguang
 */
public class Where {

    private final String field;
    private final String oper;
    private final List<Object> value = new ArrayList<>();

    public Where(String field, String oper, Object... value) {
        this.field = field;
        this.oper = oper;
        this.value.addAll(Arrays.asList(value));
    }

    /**
     * 创建Where条件
     *
     * @param field 字段
     * @param oper  操作符
     * @param value 值
     * @return Where
     */
    public static Where of(String field, String oper, Object... value) {
        return new Where(field, oper, value);
    }

    /**
     * 创建Where条件
     *
     * @param field 字段
     * @param oper  操作符
     * @return Where
     */
    public static Where of(String field, Object oper) {
        return of(field, oper.toString(), (Object) null);
    }

    // region ---- 基本运算符

    public static Where eq(String field, Object value) {
        return of(field, "=?", value);
    }

    public static Where ne(String field, Object value) {
        return of(field, "!=?", value);
    }

    public static Where gt(String field, Object value) {
        return of(field, ">?", value);
    }

    public static Where lt(String field, Object value) {
        return of(field, "<?", value);
    }

    public static Where ge(String field, Object value) {
        return of(field, ">=?", value);
    }

    public static Where le(String field, Object value) {
        return of(field, "<=?", value);
    }

    // endregion

    // region ---- like

    public static Where like(String field, String value) {
        return of(field, "LIKE '%?%'", value);
    }

    public static Where likeStart(String field, String value) {
        return of(field, "LIKE '%?'", value);
    }

    public static Where likeEnd(String field, String value) {
        return of(field, "LIKE '?%'", value);
    }

    public static Where notLike(String field, String value) {
        return of(field, "NOT LIKE %?%", value);
    }

    public static Where notLikeStart(String field, String value) {
        return of(field, "NOT LIKE %?", value);
    }

    public static Where notLikeEnd(String field, String value) {
        return of(field, "NOT LIKE ?%", value);
    }

    // endregion

    // region ---- in
    public static Where in(String field, Object... parms) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Object o : parms) {
            sb.append(" ?,");
        }
        sb.deleteCharAt(sb.length() - 1).append(")");
        return of(field, "IN " + sb, parms);

    }

    public static Where notIn(String field, Object... parms) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Object o : parms) {
            sb.append(" ?,");
        }
        sb.deleteCharAt(sb.length() - 1).append(")");
        return of(field, "NOT IN " + sb, parms);
    }

    // endregion

    // region ---- is null
    public static Where isNull(String field) {
        return of(field, "IS NULL");
    }

    public static Where isNotNull(String field) {
        return of(field, "IS NOT NULL");
    }

    // endregion

    // region ---- between

    public static Where between(String field, Object v1, Object v2) {
        return of(field, "BETWEEN ? AND ?", v1, v2);
    }

    public static Where notBetween(String field, Object v1, Object v2) {
        return of(field, "NOT BETWEEN ? AND ?", v1, v2);
    }

    // endregion

    @Override
    public String toString() {
        return new StringJoiner(", ", Where.class.getSimpleName() + "[", "]")
                .add("field='" + field + "'")
                .add("oper='" + oper + "'")
                .add("value=" + value)
                .toString();
    }

    /**
     * 获得结果
     *
     * @return Pair
     */
    public Pair<String, List<Object>> ok() {
        return Pair.of(field + " " + oper + " ", value);
    }
}
