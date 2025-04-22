package io.github.kongweiguang.db.util;

import io.github.kongweiguang.core.lang.Assert;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * ResultSet工具类
 *
 * @author kongweiguang
 */
public class RS {

    private static final int DEFAULT_MAP_CAPACITY = 16;

    private RS() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取结果集中的数据,并转换为Map
     *
     * @param rs 结果集
     * @return map
     */
    public static Map<String, Object> toMap(ResultSet rs) {
        Assert.notNull(rs, "ResultSet must not be null");


        try {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            if (!rs.next()) {
                return Collections.emptyMap();
            }

            return processRow(rs, meta, columnCount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取结果集中的数据,并转换为List
     *
     * @param rs 结果集
     * @return list
     */
    public static List<Map<String, Object>> toList(ResultSet rs) {
        Assert.notNull(rs, "ResultSet must not be null");

        try {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            List<Map<String, Object>> resultList = new ArrayList<>();

            while (rs.next()) {
                resultList.add(processRow(rs, meta, columnCount));
            }
            return resultList;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to convert result set to list", e);
        }
    }

    /**
     * 获取结果集中的数据,统计数量
     *
     * @param rs 结果集
     * @return long
     */
    public static Number count(ResultSet rs) {
        Assert.notNull(rs, "ResultSet must not be null");
        try {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return 0L;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve count value", e);
        }
    }

    private static Map<String, Object> processRow(ResultSet rs, ResultSetMetaData meta, int columnCount) throws SQLException {
        Map<String, Object> row = new HashMap<>(DEFAULT_MAP_CAPACITY);
        for (int i = 1; i <= columnCount; i++) {
            row.put(meta.getColumnLabel(i), rs.getObject(i));
        }
        return row;
    }

    public static final class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}