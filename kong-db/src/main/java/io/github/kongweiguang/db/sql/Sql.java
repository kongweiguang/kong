package io.github.kongweiguang.db.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sql {
    private final StringBuilder sql = new StringBuilder();
    private final List<Object> paramsList = new ArrayList<>();


    public static Sql of() {
        return new Sql();
    }

    public Sql select(String... columns) {
        sql.append("SELECT ");
        StringBuilder sb = new StringBuilder();
        for (String c : columns) {
            sb.append(c).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sql.append(sb).append(" ");
        return this;
    }

    public Sql from(String table) {
        sql.append("FROM ").append(table).append(" ");
        return this;
    }

    public Sql join(String table) {
        sql.append("INNER JOIN ").append(table).append(" ");
        return this;
    }

    public Sql leftJoin(String table) {
        sql.append("INNER JOIN ").append(table).append(" ");
        return this;
    }

    public Sql rightJoin(String table) {
        sql.append("INNER JOIN ").append(table).append(" ");
        return this;
    }

    public Sql fullJoin(String table) {
        sql.append("INNER JOIN ").append(table).append(" ");
        return this;
    }

    public Sql on(String where) {
        sql.append(where);
        return this;
    }

    public Sql on(Where... wheres) {
        return this;
    }


    public Sql where(String where, Object... params) {
        sql.append("WHERE ").append(where).append(" ");
        paramsList.addAll(Arrays.asList(params));
        return this;
    }

    public Sql where(Where... wheres) {
        sql.append("WHERE ");
        StringBuilder sb = new StringBuilder();
        for (Where w : wheres) {
            sb.append(w).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        return this;
    }

    public Sql groupBy(String... groups) {
        sql.append("GROUP BY ").append(String.join(",", groups));
        return this;
    }

    public Sql having(String where, Object... params) {
        sql.append("HAVING ").append(where).append(" ");
        paramsList.addAll(Arrays.asList(params));
        return this;
    }

    public Sql orderBy(Order... order) {
        sql.append("ORDER BY ");
        for (Order od : order) {
            sql.append(od.field()).append(" ").append(od.sort()).append(" ");
        }
        return this;
    }

    public Sql first(String str) {
        sql.insert(0, str);
        return this;
    }

    public Sql last(String str) {
        sql.append(str);
        return this;
    }

    // region ------ update

    public Sql update(String table) {
        sql.append("UPDATE ").append(table).append(" ");
        return this;
    }

    public Sql set(String set, String... params) {
        sql.append("SET ").append(set).append(" ");
        paramsList.addAll(Arrays.asList(params));
        return this;
    }

    // region ------ insert

    public Sql insert(String table) {
        sql.append("INSERT  INTO ").append(table).append(" (");
        return this;
    }

    public Sql into(String... columns) {
        sql.append(String.join(", ", columns)).append(") VALUES ");
        return this;
    }

    public Sql values(Object... values) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        for (Object ignore : values) {
            sb.append("?,");
        }
        paramsList.add(values);

        sb.deleteCharAt(sb.length() - 1).append(")");
        sql.append(sb);
        return this;
    }

    //region ------ delete
    public Sql deleteFrom(String table) {
        sql.append("DELETE FROM ").append(table);
        return this;
    }

    // 生成最终 SQL
    public SqlRes ok() {
        return new SqlRes(sql.toString(), paramsList.toArray());
    }

}