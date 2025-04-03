package io.github.kongweiguang.db;


import io.github.kongweiguang.core.util.IOs;
import io.github.kongweiguang.db.func.SqlRun;
import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.page.PageRes;
import io.github.kongweiguang.db.util.Rs;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DbRun {
    private final Connection con;

    public DbRun(DataSource ds) {
        try {
            this.con = ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DbRun of(DataSource ds) {
        return new DbRun(ds);
    }

    public Map<String, Object> select(String sql, Object... params) {
        PreparedStatement ps = null;
        try {
            ps = ps(sql, params);
            ps.close();
            ResultSet rs = ps.executeQuery();
            return Rs.map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IOs.close(ps);
        }
    }

    public List<Map<String, Object>> selectList(String sql, Object... params) {
        PreparedStatement ps = null;
        try {
            ps = ps(sql, params);
            ResultSet rs = ps.executeQuery();
            return Rs.list(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IOs.close(ps);

        }
    }


    public long count(String sql, Object... params) {
        PreparedStatement ps = null;
        try {
            ps = ps(sql, params);
            ResultSet rs = ps.executeQuery();
            return Rs.count(rs).longValue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IOs.close(ps);

        }
    }

    public PageRes<Map<String, Object>> page(String sql, Page page, Object... params) {
        PreparedStatement ps = null;
        try {
            long count = count(sql, params);
            sql += "LIMIT " + page.getPageNumber() + ", " + page.getPageSize();
            ps = ps(sql, params);
            ResultSet rs = ps.executeQuery();
            return PageRes.of(count, Rs.list(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IOs.close(ps);

        }
    }


    public int execute(String sql, Object... params) {
        PreparedStatement ps = null;
        try {
            ps = ps(sql, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IOs.close(ps);

        }
    }

    public void tx(SqlRun run) throws SQLException {

        final boolean auto = con.getAutoCommit();
        try {
            if (auto) {
                con.setAutoCommit(false);
            }
            run.run(this);
            con.commit();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new SQLException(ex);
            }
        } finally {
            con.setAutoCommit(auto);
        }
    }


    private PreparedStatement ps(String sql, Object[] params) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);

        if (null != params) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
        return ps;
    }
}
