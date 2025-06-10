package io.github.kongweiguang.db.run;

import io.github.kongweiguang.core.lang.IOs;
import io.github.kongweiguang.db.DB;
import io.github.kongweiguang.db.core.PS;
import io.github.kongweiguang.db.func.RsFn;
import io.github.kongweiguang.db.func.SqlRun;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库操作类
 *
 * @author kongweiguang
 */
public abstract class DbRun {
    protected static final InheritableThreadLocal<Connection> cache = new InheritableThreadLocal<>();
    protected final DataSource ds;

    protected DbRun(DataSource ds) {
        this.ds = ds;
    }

    //region 内部方法

    /**
     * 获取连接
     *
     * @return 连接
     * @throws SQLException SQL异常
     */
    protected Connection con() throws SQLException {
        Connection con = cache.get();
        if (con == null) {
            cache.set(ds.getConnection());
            con = cache.get();
        }
        return con;
    }

    /**
     * 关闭连接
     *
     * @param con 连接
     * @throws SQLException SQL异常
     */
    protected void closeCon(Connection con) throws SQLException {
        boolean autoCommit = con.getAutoCommit();
        if (!autoCommit) {
            return;
        }
        cache.remove();
        IOs.close(con);
    }

    /**
     * 执行查询
     *
     * @param sql    查询语句
     * @param rsFn   提取器
     * @param params 参数
     * @param <T>    返回对象类型
     * @return 提取器返回对象
     * @throws SQLException SQL异常
     */
    public <T> T executeQuery(String sql, RsFn<T> rsFn, Object... params) throws SQLException {
        Connection con = con();
        try {
            PreparedStatement ps = PS.of(con, sql, params);
            ResultSet rs = ps.executeQuery();
            return rsFn.handle(rs);
        } finally {
            closeCon(con);
        }
    }

    /**
     * 执行更新
     *
     * @param sql    更新语句
     * @param params 参数
     * @return 更新行数
     * @throws SQLException SQL异常
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection con = con();
        try {
            PreparedStatement ps = PS.of(con, sql, params);
            return ps.executeUpdate();
        } finally {
            closeCon(con);
        }
    }

    /**
     * 批量执行更新
     *
     * @param sql    更新语句
     * @param params 参数
     * @return 更新行数
     * @throws SQLException SQL异常
     */
    public int[] executeBatch(String sql, List<Object[]> params) throws SQLException {
        Connection con = con();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            for (Object[] param : params) {
                for (int i = 0; i < param.length; i++) {
                    ps.setObject(i + 1, param[i]);
                }
                ps.addBatch();
            }
            return ps.executeBatch();
        } finally {
            closeCon(con);
        }
    }

    //endregion

    /**
     * 事务
     *
     * @param run 事务对象
     * @throws SQLException SQL异常
     */
    public void tx(SqlRun run) throws SQLException {
        Connection con = con();
        try {
            con.setAutoCommit(false);
            try {
                run.run((DB) this);
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } finally {
            con.setAutoCommit(true);
            closeCon(con);
        }
    }

}