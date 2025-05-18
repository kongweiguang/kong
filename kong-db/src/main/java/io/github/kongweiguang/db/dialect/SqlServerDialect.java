package io.github.kongweiguang.db.dialect;

import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.sql.SqlRes;

/**
 * SQL Server数据库方言
 * 提供SQL Server特定的SQL生成功能
 *
 * @author kongweiguang
 */
public class SqlServerDialect implements Dialect {
    @Override
    public SqlRes genPageSql(String sql, Page page, Object[] params) {
        int limit = page.pageSize();
        int offset = (page.pageNumber() - 1) * limit;

        // SQL Server 2012及以上版本支持OFFSET FETCH语法
        String pageSql = sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        Object[] allParams = new Object[params.length + 2];
        System.arraycopy(params, 0, allParams, 0, params.length);

        allParams[params.length] = offset;
        allParams[params.length + 1] = limit;

        return new SqlRes(pageSql, allParams);
    }
}