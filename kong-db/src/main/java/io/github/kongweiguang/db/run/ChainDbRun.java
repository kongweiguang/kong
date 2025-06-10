package io.github.kongweiguang.db.run;

import io.github.kongweiguang.db.core.RS;
import io.github.kongweiguang.db.dialect.Dialect;
import io.github.kongweiguang.db.execption.KongSqlException;
import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.page.PageRes;
import io.github.kongweiguang.db.sql.SqlRes;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 链式数据库操作类
 *
 * @author kongweiguang
 */
@SuppressWarnings("unchecked")
public class ChainDbRun extends DbRun {
    protected SqlRes sqlRes;

    protected ChainDbRun(DataSource ds, SqlRes sqlRes) {
        super(ds);
        this.sqlRes = sqlRes;
    }

    /**
     * 创建链式数据库操作类
     *
     * @param ds     数据源
     * @param sqlRes SQL对象
     * @return 链式数据库操作类
     */
    public static ChainDbRun of(DataSource ds, SqlRes sqlRes) {
        return new ChainDbRun(ds, sqlRes);
    }


    /**
     * 获取单条对象，默认是map
     * 注意：如果查询结果为空，会返回null
     *
     * @return map
     */
    public Map<String, Object> one() {
        return (Map<String, Object>) one(Map.class);
    }

    /**
     * 获取单条对象
     * 注意：如果查询结果为空，会返回null
     *
     * @param clazz 类型
     * @return 对象
     */
    public <T> T one(Class<T> clazz) {
        try {
            return executeQuery(sqlRes.sql(), rs -> RS.toBean(rs, clazz), sqlRes.params());
        } catch (SQLException e) {
            throw new KongSqlException(e);
        }
    }


    /**
     * 获取列表
     *
     * @return 列表
     */
    public List<Map<String, Object>> list() {
        return list(Map.class);
    }

    /**
     * 获取列表
     *
     * @param clazz 类型
     * @return 列表
     */
    public <T> List<T> list(Class<?> clazz) {
        try {
            return (List<T>) executeQuery(sqlRes.sql(), rs -> RS.toList(rs, clazz), sqlRes.params());
        } catch (SQLException e) {
            throw new KongSqlException(e);
        }
    }

    /**
     * 获取单个值
     * 注意：如果查询结果为空，会返回null
     *
     * @return 值
     */
    public <T> T value() {
        try {
            return executeQuery(sqlRes.sql(), RS::value, sqlRes.params());
        } catch (SQLException e) {
            throw new KongSqlException(e);
        }
    }

    /**
     * 判断是否存在
     *
     * @return true:存在 false:不存在
     */
    public boolean exist() {
        try {
            return executeQuery(sqlRes.sql(), RS::exist, sqlRes.params());
        } catch (SQLException e) {
            throw new KongSqlException(e);
        }
    }

    /**
     * 统计数量
     *
     * @return 数量
     */
    public Long count() {
        try {
            return executeQuery(sqlRes.sql(), RS::value, sqlRes.params());
        } catch (SQLException e) {
            throw new KongSqlException(e);
        }
    }

    /**
     * 分页查询
     * 默认从第1页开始分页大小为10
     *
     * @return 分页结果
     */
    public PageRes<Map<String, Object>> page() {
        return page(Page.of(1, 10));
    }

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @return 分页结果
     */
    public PageRes<Map<String, Object>> page(Page page) {
        return page(page, Map.class);
    }

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param clazz 类型
     * @return 分页结果
     */
    public <T> PageRes<T> page(Page page, Class<?> clazz) {
        try {
            String countSql = "SELECT COUNT(*) FROM (" + sqlRes.sql() + ") AS count_table";
            Long totalCount = executeQuery(countSql, RS::value, sqlRes.params());

            // 获取数据库类型并生成对应的分页SQL和参数
            Dialect dialect = Dialect.getDatabaseDialect(con());
            SqlRes sr = dialect.genPageSql(sqlRes.sql(), page, sqlRes.params());

            return (PageRes<T>) PageRes.of(totalCount, executeQuery(sr.sql(), rs -> RS.toList(rs, clazz), sr.params()));
        } catch (SQLException e) {
            throw new KongSqlException(e);
        }
    }

    /**
     * 执行sql
     *
     * @return 影响行数
     */
    public int exec() {
        try {
            return executeUpdate(sqlRes.sql(), sqlRes.params());
        } catch (SQLException e) {
            throw new KongSqlException(e);
        }
    }

    /**
     * 执行批量更新
     *
     * @return 影响行数
     */
    public int[] batch(List<Object[]> params) {
        try {
            return executeBatch(sqlRes.sql(), params);
        } catch (SQLException e) {
            throw new KongSqlException(e);
        }
    }

}
