package io.github.kongweiguang.db.dialect;

import io.github.kongweiguang.core.lang.Strs;
import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.sql.SqlRes;

/**
 * DB2数据库方言
 *
 * @author kongweiguang
 */
public class Db2Dialect implements Dialect {
    @Override
    public SqlRes genPageSql(String sql, Page page, Object[] params) {
        int limit = page.pageSize();
        int offset = (page.pageNumber() - 1) * limit;

        // DB2分页使用ROW_NUMBER()函数

        String pageSql = Strs.fmt("""
                SELECT * FROM (SELECT temp_table.*, ROW_NUMBER() OVER() AS ROW_NUM FROM ({}) AS temp_table) WHERE ROW_NUM BETWEEN ? AND ?
                """, sql);

        Object[] allParams = new Object[params.length + 2];
        System.arraycopy(params, 0, allParams, 0, params.length);

        // 起始行（包含）
        allParams[params.length] = offset + 1;
        // 结束行（包含）
        allParams[params.length + 1] = offset + limit;

        return new SqlRes(pageSql, allParams);
    }
}