package io.github.kongweiguang.db.sql;


import io.github.kongweiguang.core.util.Strs;

import java.util.List;

public class Where {

    // 比较运算符
    public static final String EQUALS = "=";
    public static final String NOT_EQUALS = "<>";
    public static final String GREATER_THAN = ">";
    public static final String LESS_THAN = "<";
    public static final String GREATER_THAN_OR_EQUAL = ">=";
    public static final String LESS_THAN_OR_EQUAL = "<=";

    // 逻辑运算符
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";

    // 模糊查询
    public static final String LIKE = "LIKE";
    public static final String NOT_LIKE = "NOT LIKE";

    // BETWEEN
    public static final String BETWEEN = "BETWEEN";
    public static final String NOT_BETWEEN = "NOT BETWEEN";

    // 存在性检查
    public static final String EXISTS = "EXISTS";
    public static final String NOT_EXISTS = "NOT EXISTS";


    private String field;
    private String oper;
    private String value;


    public Where(String field, String oper, String value) {
        this.field = field;
        this.oper = oper;
        this.value = value;
    }

    public static Where of(String field, String oper, String value) {
        return new Where(field, oper, value);
    }

    public static Where of(String field, String oper) {
        return of(field, oper, null);
    }


    public static Where in(String field, List<Object> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Object o : list) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1).append(")");
        return of(field, "IN", sb.toString());

    }

    public static Where notIn(String field, List<Object> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Object o : list) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1).append(")");
        return of(field, "NOT IN", sb.toString());
    }

    public static Where isNull(String field) {
        return of(field, "IS NULL");
    }

    public static Where isNotNull(String field) {
        return of(field, "IS NOT NULL");
    }

    public static Where between(String field, Object v1, Object v2) {
        return of(field, "BETWEEN", "? AND ?");
    }

    public String field() {
        return field;
    }

    public String oper() {
        return oper;
    }

    public String value() {
        return value;
    }


    public static Where group() {
        return null;
    }

    public String ok() {
        StringBuilder sb = new StringBuilder();
        sb.append(field).append(' ').append(oper).append(' ');
        if (!Strs.isEmpty(value)) {
            sb.append(value);
        }
        return sb.toString();
    }
}
