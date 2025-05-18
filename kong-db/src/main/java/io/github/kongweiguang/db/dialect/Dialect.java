package io.github.kongweiguang.db.dialect;

import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.sql.SqlRes;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库方言接口
 * 提供各种数据库特定操作的实现
 *
 * @author kongweiguang
 */
public interface Dialect {
    /**
     * 生成分页SQL
     *
     * @param sql    原始SQL
     * @param page   分页对象
     * @param params 原始参数
     * @return 带有分页的SQL和参数
     */
    SqlRes genPageSql(String sql, Page page, Object[] params);

    /**
     * 获取数据库方言
     *
     * @param con 数据库连接
     * @return 数据库方言
     * @throws SQLException SQL异常
     */
    static Dialect getDatabaseDialect(Connection con) throws SQLException {
        String dbProductName = con.getMetaData().getDatabaseProductName().toLowerCase();

        if (dbProductName.contains("mysql") || dbProductName.contains("mariadb")) {
            return new MySqlDialect();
        } else if (dbProductName.contains("postgresql")) {
            return new PostgreSqlDialect();
        } else if (dbProductName.contains("oracle")) {
            return new OracleDialect();
        } else if (dbProductName.contains("sqlserver") || dbProductName.contains("microsoft")) {
            return new SqlServerDialect();
        } else if (dbProductName.contains("db2")) {
            return new Db2Dialect();
        } else if (dbProductName.contains("h2")) {
            return new H2Dialect();
        } else if (dbProductName.contains("sqlite")) {
            return new SqliteDialect();
        } else {
            // 默认使用MySQL方言
            return new MySqlDialect();
        }
    }
}