package io.github.kongweiguang.db.run;

import io.github.kongweiguang.core.lang.IOs;
import io.github.kongweiguang.db.dialect.Dialect;
import io.github.kongweiguang.db.func.RsFn;
import io.github.kongweiguang.db.func.SqlRun;
import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.page.PageRes;
import io.github.kongweiguang.db.sql.SqlRes;
import io.github.kongweiguang.db.util.RS;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作类
 *
 * @author kongweiguang
 */
public class DbRun {
    private static final InheritableThreadLocal<Connection> cache = new InheritableThreadLocal<>();
    private final DataSource ds;

    public DbRun(DataSource ds) {
        this.ds = ds;
    }

    /**
     * 创建DbRun对象
     *
     * @param ds 数据源
     * @return DbRun对象
     */
    public static DbRun of(DataSource ds) {
        return new DbRun(ds);
    }

    /**
     * 获取连接
     *
     * @return 连接
     * @throws SQLException SQL异常
     */
    public Connection con() throws SQLException {
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
    public void closeCon(Connection con) throws SQLException {
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
     * @param sql       查询语句
     * @param extractor 提取器
     * @param params    参数
     * @param <T>       返回对象类型
     * @return 提取器返回对象
     * @throws SQLException SQL异常
     */
    private <T> T executeQuery(String sql, RsFn<T> extractor, Object... params) throws SQLException {
        Connection con = con();
        try {
            PreparedStatement ps = prepareStatement(con, sql, params);
            ResultSet rs = ps.executeQuery();
            return extractor.hd(rs);
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
    private int executeUpdate(String sql, Object... params) throws SQLException {
        Connection con = con();
        try {
            PreparedStatement ps = prepareStatement(con, sql, params);
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
    private int[] executeBatch0(String sql, List<Object[]> params) throws SQLException {
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

    /**
     * 执行查询
     *
     * @param sql    查询语句
     * @param params 参数
     * @return 查询结果
     * @throws SQLException SQL异常
     */
    public Map<String, Object> select(String sql, Object... params) throws SQLException {
        return executeQuery(sql, RS::toMap, params);
    }

    /**
     * 执行查询
     *
     * @param sql    查询语句
     * @param params 参数
     * @return 查询结果
     * @throws SQLException SQL异常
     */
    public List<Map<String, Object>> selectList(String sql, Object... params) throws SQLException {
        return executeQuery(sql, RS::toList, params);
    }

    /**
     * 执行查询
     *
     * @param sql    查询语句
     * @param params 参数
     * @return 查询结果
     * @throws SQLException SQL异常
     */
    public long count(String sql, Object... params) throws SQLException {
        return executeQuery(sql, rs -> RS.count(rs).longValue(), params);
    }

    /**
     * 执行分页查询
     *
     * @param sql    查询语句
     * @param page   分页对象
     * @param params 参数
     * @return 分页结果
     * @throws SQLException SQL异常
     */
    public PageRes<Map<String, Object>> page(String sql, Page page, Object... params) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") AS count_table";
        long totalCount = count(countSql, params);

        // 获取数据库类型并生成对应的分页SQL和参数
        Dialect dialect = Dialect.getDatabaseDialect(con());
        SqlRes sr = dialect.genPageSql(sql, page, params);

        return PageRes.of(totalCount, executeQuery(sr.sql(), RS::toList, sr.params()));
    }

    /**
     * 执行更新
     *
     * @param sql    更新语句
     * @param params 参数
     * @return 更新行数
     * @throws SQLException SQL异常
     */
    public int execute(String sql, Object... params) throws SQLException {
        return executeUpdate(sql, params);
    }

    /**
     * 执行更新
     *
     * @param sql    更新语句
     * @param params 参数
     * @return 更新行数
     * @throws SQLException SQL异常
     */
    public int[] executeBatch(String sql, List<Object[]> params) throws SQLException {
        return executeBatch0(sql, params);
    }

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
                run.run(this);
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

    /**
     * 创建PreparedStatement
     *
     * @param con    连接
     * @param sql    查询语句
     * @param params 参数
     * @return PreparedStatement
     * @throws SQLException SQL异常
     */
    private PreparedStatement prepareStatement(Connection con, String sql, Object[] params) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
        return ps;
    }

}