package io.github.kongweiguang.db.sql;

import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.db.util.Wheres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Sql对象
 *
 * @author kongweiguang
 */
public class Sql {
    private final StringBuilder sql = new StringBuilder();
    private final List<Object> paramsList = new ArrayList<>();
    private boolean insertFlag = false;

    /**
     * 创建Sql对象
     *
     * @return Sql对象
     */
    public static Sql of() {
        return new Sql();
    }

    /**
     * 查询
     *
     * @param columns 列名
     * @return Sql对象
     */
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

    /**
     * 查询表
     *
     * @param table 表名
     * @return Sql对象
     */
    public Sql from(String table) {
        sql.append("FROM ").append(table).append(" ");
        return this;
    }

    /**
     * 内连接
     *
     * @param table 表名
     * @return Sql对象
     */
    public Sql innerJoin(String table) {
        sql.append("INNER JOIN ").append(table).append(" ");
        return this;
    }

    /**
     * 左连接
     *
     * @param table 表名
     * @return Sql对象
     */
    public Sql leftJoin(String table) {
        sql.append("INNER JOIN ").append(table).append(" ");
        return this;
    }

    /**
     * 右连接
     *
     * @param table 表名
     * @return Sql对象
     */
    public Sql rightJoin(String table) {
        sql.append("INNER JOIN ").append(table).append(" ");
        return this;
    }

    /**
     * 全连接
     *
     * @param table 表名
     * @return Sql对象
     */
    public Sql fullJoin(String table) {
        sql.append("INNER JOIN ").append(table).append(" ");
        return this;
    }

    /**
     * 连接条件
     *
     * @param where  条件
     * @param params 参数
     * @return Sql对象
     */
    public Sql on(String where, Object... params) {
        sql.append("ON ").append(where).append(" ");
        paramsList.addAll(Arrays.asList(params));
        return this;
    }

    /**
     * 连接条件
     *
     * @param wheres 条件
     * @return Sql对象
     */
    public Sql on(Where... wheres) {
        StringBuilder sb = new StringBuilder();
        sb.append("ON ");
        Pair<String, List<Object>> p = Wheres.buildWhere(wheres);
        sb.append(p.k());
        paramsList.addAll(p.v());
        sql.append(sb);
        return this;
    }

    public Sql on(WhereGroup... wheres) {
        StringBuilder sb = new StringBuilder();
        sb.append("ON ");
        Pair<String, List<Object>> p = Wheres.buildGroup(wheres);
        sb.append(p.k());
        paramsList.addAll(p.v());
        sql.append(sb);
        return this;
    }

    /**
     * 查询条件
     *
     * @param where  条件
     * @param params 参数
     * @return Sql对象
     */
    public Sql where(String where, Object... params) {
        sql.append("WHERE ").append(where).append(" ");
        paramsList.addAll(Arrays.asList(params));
        return this;
    }

    /**
     * 查询条件
     *
     * @param wheres 条件
     * @return Sql对象
     */
    public Sql where(Where... wheres) {
        StringBuilder sb = new StringBuilder();
        sb.append("WHERE ");
        Pair<String, List<Object>> p = Wheres.buildWhere(wheres);
        sql.append(sb).append(p.k());
        paramsList.addAll(p.v());
        sql.append(sb);
        return this;
    }

    /**
     * 添加分组条件
     *
     * @param wheres 条件
     * @return Sql对象
     */
    public Sql where(WhereGroup... wheres) {
        StringBuilder sb = new StringBuilder();
        sb.append("WHERE ");
        Pair<String, List<Object>> p = Wheres.buildGroup(wheres);
        sb.append(p.k());
        paramsList.addAll(p.v());
        sql.append(sb);
        return this;
    }

    /**
     * 分组
     *
     * @param groups 分组字段
     * @return Sql对象
     */
    public Sql groupBy(String... groups) {
        sql.append("GROUP BY ").append(String.join(",", groups));
        return this;
    }

    /**
     * 分组条件
     *
     * @param where  条件
     * @param params 参数
     * @return Sql对象
     */
    public Sql having(String where, Object... params) {
        sql.append("HAVING ").append(where).append(" ");
        paramsList.addAll(Arrays.asList(params));
        return this;
    }

    /**
     * 分组条件
     *
     * @param wheres 条件
     * @return Sql对象
     */
    public Sql having(Where... wheres) {
        StringBuilder sb = new StringBuilder();
        sb.append("HAVING ");
        Pair<String, List<Object>> p = Wheres.buildWhere(wheres);
        sb.append(p.k());
        paramsList.addAll(p.v());
        sql.append(sb);
        return this;
    }

    /**
     * 添加分组条件
     *
     * @param wheres 条件
     * @return Sql对象
     */
    public Sql having(WhereGroup... wheres) {
        StringBuilder sb = new StringBuilder();
        sb.append("HAVING ");
        Pair<String, List<Object>> p = Wheres.buildGroup(wheres);
        sb.append(p.k());
        paramsList.addAll(p.v());
        sql.append(sb);
        return this;
    }

    /**
     * 排序
     *
     * @param order 排序字段
     * @return Sql对象
     */
    public Sql orderBy(Order... order) {
        sql.append("ORDER BY ");
        for (Order od : order) {
            sql.append(od.field()).append(" ").append(od.sort()).append(" ");
        }
        return this;
    }

    /**
     * 首行
     *
     * @param str 字段
     * @return Sql对象
     */
    public Sql first(String str) {
        sql.insert(0, str).append(" ");
        return this;
    }

    /**
     * 末行
     *
     * @param str 字段
     * @return Sql对象
     */
    public Sql last(String str) {
        sql.append(str).append(" ");
        return this;
    }

    // region ------ update

    /**
     * 更新
     *
     * @param table 表名
     * @return Sql对象
     */
    public Sql update(String table) {
        sql.append("UPDATE ").append(table).append(" ").append("SET ");
        return this;
    }

    /**
     * 设置
     *
     * @param set    设置字段
     * @param params 参数
     * @return Sql对象
     */
    public Sql set(String set, String... params) {
        sql.append(set).append(" ");
        paramsList.addAll(Arrays.asList(params));
        return this;
    }

    /**
     * 设置
     *
     * @param sets 设置字段
     * @return Sql对象
     */
    public Sql set(Pair<?, ?>... sets) {
        for (Pair<?, ?> set : sets) {
            sql.append(set.k()).append(" = ?,");
            paramsList.add(set.v());
        }
        return this;
    }

    // region ------ insert

    /**
     * 插入
     *
     * @param table 表名
     * @return Sql对象
     */
    public Sql insert(String table) {
        sql.append("INSERT  INTO ").append(table).append(" ");
        return this;
    }

    /**
     * 插入
     *
     * @param columns 列名
     * @return Sql对象
     */
    public Sql into(String... columns) {
        sql.append(" (").append(String.join(", ", columns)).append(") VALUES ");
        return this;
    }


    /**
     * 多条插入 *
     *
     * @param values
     * @return
     */
    public Sql values(List<Object[]> values) {
        if (insertFlag) {
            sql.append(",");
        }

        for (Object[] value : values) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (Object o : value) {
                sb.append("?,");
            }
            paramsList.addAll(Arrays.asList(value));
            sb.deleteCharAt(sb.length() - 1).append("),");
            sql.append(sb);
            sb.setLength(0);
        }

        sql.deleteCharAt(sql.length() - 1);
        //判断是不是多次调用
        insertFlag = true;
        return this;
    }

    /**
     * 单条插入
     *
     * @param values
     * @return
     */
    public Sql value(Object... values) {
        return values(Collections.singletonList(values));
    }

    //region ------ delete

    /**
     * 删除
     *
     * @param table 表名
     * @return Sql对象
     */
    public Sql deleteFrom(String table) {
        sql.append("DELETE FROM ").append(table).append(" ");
        return this;
    }

    // 生成最终 SQL
    public SqlRes ok() {
        return new SqlRes(sql.toString(), paramsList.toArray());
    }

}