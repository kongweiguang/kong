package io.github.kongweiguang.db.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rs {

    public static Map<String, Object> map(ResultSet rs) {
        try {
            Map<String, Object> map = new HashMap<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    map.put(columnName, value);
                }
            }
            return map;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<Map<String, Object>> list(ResultSet rs) {
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> rowMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    rowMap.put(columnName, value);
                }
                list.add(rowMap);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Number count(ResultSet rs) {
        try {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
