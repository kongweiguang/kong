package io.github.kongweiguang.core.file;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * TOML工具类
 * 提供统一的接口来读取和写入TOML文件
 *
 * @author kongweiguang
 */
public class Tomls {
    // 读取相关常量
    private static final char COMMENT_CHAR = '#';
    private static final char TABLE_START = '[';
    private static final char TABLE_END = ']';
    private static final char QUOTE_DOUBLE = '"';
    private static final char QUOTE_SINGLE = '\'';
    private static final char EQUALS = '=';

    /**
     * 将TOML文件读取为Map
     *
     * @param file TOML文件路径
     * @return 解析后的Map
     * @throws IOException 如果文件读取失败
     */
    public static Map<String, Object> toMap(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            return parseToml(reader);
        }
    }

    /**
     * 将TOML内容字符串解析为Map
     *
     * @param content TOML内容字符串
     * @return 解析后的Map
     */
    public static Map<String, Object> toMap(String content) {
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            return parseToml(reader);
        } catch (IOException e) {
            throw new RuntimeException("parse toml str fail", e);
        }
    }

    /**
     * 将Map写入TOML文件
     *
     * @param data     数据Map
     * @param filePath 输出文件路径
     * @throws IOException 如果写入失败
     */
    public static File toFile(Map<String, Object> data, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            writeToml(data, writer, "");
            return new File(filePath);
        }
    }

    /**
     * 将Map转换为TOML格式字符串
     *
     * @param data 数据Map
     * @return TOML格式的字符串
     */
    public static String toTomlStr(Map<String, Object> data) {
        try (StringWriter stringWriter = new StringWriter();
             BufferedWriter writer = new BufferedWriter(stringWriter)) {

            writeToml(data, writer, "");
            writer.flush();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException("convert map to toml str fail ", e);
        }
    }

    // ------------------- 读取实现 -------------------

    /**
     * 从BufferedReader解析TOML内容
     */
    private static Map<String, Object> parseToml(BufferedReader reader) throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> currentTable = result;
        String currentTableName = "";

        String line;
        while ((line = reader.readLine()) != null) {
            // 预处理行内容：去除空格和注释
            line = line.trim();
            if (line.isEmpty() || line.charAt(0) == COMMENT_CHAR) {
                continue;
            }

            // 处理表头 [table] 或 [[array_of_tables]]
            if (line.charAt(0) == TABLE_START) {
                boolean isArrayOfTables = line.startsWith("[[") && line.endsWith("]]");
                String tableName;

                if (isArrayOfTables) {
                    tableName = line.substring(2, line.length() - 2).trim();
                } else if (line.endsWith("]")) {
                    tableName = line.substring(1, line.length() - 1).trim();
                } else {
                    throw new IllegalArgumentException("无效的表定义: " + line);
                }

                if (isArrayOfTables) {
                    // 处理表数组 [[array_of_tables]]
                    List<Map<String, Object>> tables = getOrCreateTableArray(result, tableName);
                    Map<String, Object> newTable = new LinkedHashMap<>();
                    tables.add(newTable);
                    currentTable = newTable;
                } else {
                    // 处理普通表 [table]
                    currentTable = getOrCreateTable(result, tableName);
                }
                currentTableName = tableName;
                continue;
            }

            // 处理键值对
            int equalsPos = line.indexOf(EQUALS);
            if (equalsPos > 0) {
                String key = line.substring(0, equalsPos).trim();
                String value = line.substring(equalsPos + 1).trim();

                Object parsedValue = parseValue(value);
                currentTable.put(key, parsedValue);
            }
        }

        return result;
    }

    /**
     * 解析值部分
     */
    private static Object parseValue(String value) {
        if (value.isEmpty()) {
            return "";
        }

        // 处理字符串
        if ((value.charAt(0) == QUOTE_DOUBLE && value.charAt(value.length() - 1) == QUOTE_DOUBLE) ||
            (value.charAt(0) == QUOTE_SINGLE && value.charAt(value.length() - 1) == QUOTE_SINGLE)) {
            return unescapeString(value.substring(1, value.length() - 1));
        }

        // 处理数组
        if (value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']') {
            return parseArray(value);
        }

        // 处理布尔值
        if ("true".equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        }

        // 处理数字
        try {
            // 尝试解析为整数
            if (!value.contains(".")) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return Long.parseLong(value);
                }
            }
            // 尝试解析为浮点数
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // 如果不是数字，当作字符串处理
            return value;
        }
    }

    /**
     * 解析数组值
     */
    private static List<Object> parseArray(String arrayStr) {
        List<Object> result = new ArrayList<>();
        if (arrayStr.length() <= 2) {
            return result; // 空数组
        }

        String content = arrayStr.substring(1, arrayStr.length() - 1).trim();
        if (content.isEmpty()) {
            return result;
        }

        // 解析数组元素
        StringBuilder element = new StringBuilder();
        boolean inString = false;
        char quoteChar = 0;
        int bracketDepth = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            // 处理字符串
            if ((c == QUOTE_DOUBLE || c == QUOTE_SINGLE) && (i == 0 || content.charAt(i - 1) != '\\')) {
                if (!inString) {
                    inString = true;
                    quoteChar = c;
                } else if (c == quoteChar) {
                    inString = false;
                }
            }

            // 处理嵌套数组
            if (!inString) {
                if (c == '[') {
                    bracketDepth++;
                } else if (c == ']') {
                    bracketDepth--;
                }
            }

            // 只在非字符串内部且无嵌套数组时处理逗号
            if (c == ',' && !inString && bracketDepth == 0) {
                result.add(parseValue(element.toString().trim()));
                element.setLength(0); // 清空StringBuilder
                continue;
            }

            element.append(c);
        }

        // 添加最后一个元素
        if (element.length() > 0) {
            result.add(parseValue(element.toString().trim()));
        }

        return result;
    }

    /**
     * 获取或创建嵌套表
     */
    private static Map<String, Object> getOrCreateTable(Map<String, Object> root, String tableName) {
        if (tableName.isEmpty()) {
            return root;
        }

        Map<String, Object> current = root;
        for (String part : tableName.split("\\.")) {
            part = part.trim();
            Object existingObj = current.get(part);

            if (existingObj == null) {
                Map<String, Object> newTable = new LinkedHashMap<>();
                current.put(part, newTable);
                current = newTable;
            } else if (existingObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> existingTable = (Map<String, Object>) existingObj;
                current = existingTable;
            } else {
                throw new IllegalArgumentException("key '" + part + "' exists but is not a table");
            }
        }

        return current;
    }

    /**
     * 获取或创建表数组
     */
    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> getOrCreateTableArray(Map<String, Object> root, String tableName) {
        // 分解表名路径
        String[] parts = tableName.split("\\.");
        String lastPart = parts[parts.length - 1].trim();

        // 构建父表路径
        StringBuilder parentPathBuilder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) {
                parentPathBuilder.append(".");
            }
            parentPathBuilder.append(parts[i].trim());
        }
        String parentPath = parentPathBuilder.toString();

        // 获取或创建父表
        Map<String, Object> parent;
        if (parentPath.isEmpty()) {
            parent = root;
        } else {
            parent = getOrCreateTable(root, parentPath);
        }

        // 获取或创建表数组
        Object existing = parent.get(lastPart);
        if (existing == null) {
            List<Map<String, Object>> newArray = new ArrayList<>();
            parent.put(lastPart, newArray);
            return newArray;
        } else if (existing instanceof List) {
            return (List<Map<String, Object>>) existing;
        } else {
            throw new IllegalArgumentException("key '" + lastPart + "' exists but is not a table array");
        }
    }

    /**
     * 反转义字符串
     */
    private static String unescapeString(String str) {
        if (!str.contains("\\")) {
            return str;
        }

        StringBuilder result = new StringBuilder(str.length());
        boolean escape = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (escape) {
                switch (c) {
                    case 'b':
                        result.append('\b');
                        break;
                    case 't':
                        result.append('\t');
                        break;
                    case 'n':
                        result.append('\n');
                        break;
                    case 'f':
                        result.append('\f');
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    case '"':
                        result.append('"');
                        break;
                    case '\'':
                        result.append('\'');
                        break;
                    case '\\':
                        result.append('\\');
                        break;
                    default:
                        result.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    // ------------------- 写入实现 -------------------

    /**
     * 递归写入TOML格式
     */
    private static void writeToml(Map<String, Object> data, BufferedWriter writer, String prefix) throws IOException {
        // 先写入简单键值对
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 跳过复杂类型（表和数组），留到后面处理
            if (value instanceof Map) {
                continue;
            }
            if (value instanceof List && !((List<?>) value).isEmpty() && ((List<?>) value).get(0) instanceof Map) {
                continue;
            }

            writer.write(key);
            writer.write(" = ");
            writeValue(value, writer);
            writer.newLine();
        }

        // 如果有简单键值对，在表之前添加一个空行
        boolean hasSimpleValues = false;
        for (Object value : data.values()) {
            if (!(value instanceof Map) &&
                !(value instanceof List && !((List<?>) value).isEmpty() && ((List<?>) value).get(0) instanceof Map)) {
                hasSimpleValues = true;
                break;
            }
        }

        if (hasSimpleValues) {
            writer.newLine();
        }

        // 然后写入嵌套表
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 处理单个表
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tableValue = (Map<String, Object>) value;

                String tableName = prefix.isEmpty() ? key : prefix + "." + key;
                writer.write("[");
                writer.write(tableName);
                writer.write("]");
                writer.newLine();
                writeToml(tableValue, writer, tableName);
                writer.newLine();
            }

            // 处理表数组
            if (value instanceof List && !((List<?>) value).isEmpty() && ((List<?>) value).get(0) instanceof Map) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tableArray = (List<Map<String, Object>>) value;

                String tableName = prefix.isEmpty() ? key : prefix + "." + key;
                for (Map<String, Object> tableItem : tableArray) {
                    writer.write("[[");
                    writer.write(tableName);
                    writer.write("]]");
                    writer.newLine();
                    writeToml(tableItem, writer, tableName);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * 写入值
     */
    private static void writeValue(Object value, BufferedWriter writer) throws IOException {
        switch (value) {
            case null -> writer.write("null");
            case String s -> writeString(s, writer);
            case Number number -> writer.write(value.toString());
            case Boolean b -> writer.write(value.toString());
            case Collection<?> collection -> writeArray(collection, writer);
            default ->
                // 对于其他类型，使用其字符串表示并加引号
                    writeString(value.toString(), writer);
        }
    }

    /**
     * 写入字符串值，包括转义
     */
    private static void writeString(String str, BufferedWriter writer) throws IOException {
        writer.write('"');

        // 使用字符替换而非正则表达式以提高性能
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            switch (c) {
                case '\\':
                    writer.write("\\\\");
                    break;
                case '"':
                    writer.write("\\\"");
                    break;
                case '\b':
                    writer.write("\\b");
                    break;
                case '\t':
                    writer.write("\\t");
                    break;
                case '\n':
                    writer.write("\\n");
                    break;
                case '\f':
                    writer.write("\\f");
                    break;
                case '\r':
                    writer.write("\\r");
                    break;
                default:
                    if (c < 32) {
                        writer.write(String.format("\\u%04x", (int) c));
                    } else {
                        writer.write(c);
                    }
            }
        }

        writer.write('"');
    }

    /**
     * 写入数组值
     */
    private static void writeArray(Collection<?> collection, BufferedWriter writer) throws IOException {
        writer.write('[');

        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                writer.write(", ");
            }
            writeValue(item, writer);
            first = false;
        }

        writer.write(']');
    }
}
