package io.github.kongweiguang.db.dialect;

import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.sql.SqlRes;

/**
 * MySQL数据库方言
 * 提供MySQL特定的SQL生成功能
 *
 * @author kongweiguang
 */
public class MySqlDialect implements Dialect {

    @Override
    public SqlRes genPageSql(String sql, Page page, Object[] params) {
        String pageSql = sql + " LIMIT ? OFFSET ?";

        Object[] allParams = new Object[params.length + 2];
        System.arraycopy(params, 0, allParams, 0, params.length);

        int limit = page.pageSize();
        int offset = (page.pageNumber() - 1) * limit;

        allParams[params.length] = limit;
        allParams[params.length + 1] = offset;

        return new SqlRes(pageSql, allParams);
    }

}