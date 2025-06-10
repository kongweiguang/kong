package io.github.kongweiguang.db.core;

import io.github.kongweiguang.core.map.Maps;
import io.github.kongweiguang.db.execption.DataAccessException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ResultSet工具类
 *
 * @author kongweiguang
 */
@SuppressWarnings({"unused", "unchecked"})
public class RS {

    /**
     * 获取结果集中的数据,并转换为Map
     *
     * @param rs 结果集
     * @return map
     */
    public static <T> T toBean(ResultSet rs, Class<T> clazz) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            if (!rs.next()) {
                return null;
            }

            Map<String, Object> map = processRow(rs, meta, columnCount);
            if (clazz.isAssignableFrom(Map.class)) {
                return (T) map;
            }

            return Maps.toBean(map, clazz);
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
    public static <T> List<T> toList(ResultSet rs, Class<T> clazz) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            List<Map<String, Object>> resultList = new ArrayList<>();

            while (rs.next()) {
                resultList.add(processRow(rs, meta, columnCount));
            }

            return Maps.toBeanList(resultList, clazz);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to convert result set to list", e);
        }
    }

    private static Map<String, Object> processRow(ResultSet rs, ResultSetMetaData meta, int columnCount) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        for (int i = 1; i <= columnCount; i++) {
            row.put(meta.getColumnLabel(i), rs.getObject(i));
        }
        return row;
    }

    /**
     * 判断结果集中是否有数据
     *
     * @param rs 结果集
     * @return boolean
     */
    public static Boolean exist(ResultSet rs) {
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve count value", e);
        }
    }

    /**
     * 获取结果集中的单个数据
     *
     * @param rs 结果集
     * @return map
     */
    public static <T> T value(ResultSet rs) {
        try {
            if (rs.next()) {
                return (T) rs.getObject(1);
            }

            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to convert result set to value", e);
        }
    }
}