package io.github.kongweiguang.db.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PreparedStatement
 *
 * @author kongweiguang
 */
public class PS {

    /**
     * 创建PreparedStatement
     *
     * @param con    连接
     * @param sql    查询语句
     * @param params 参数
     * @return PreparedStatement
     * @throws SQLException SQL异常
     */
    public static PreparedStatement of(Connection con, String sql, Object[] params) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
        return ps;
    }
}
