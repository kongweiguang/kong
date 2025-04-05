package io.github.kongweiguang.core.util;

import java.util.Map;

import static java.util.Objects.isNull;

/**
 * 字符串工具类
 *
 * @author kongweiguang
 */
public class Strs {

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(final String str) {
        if (isNull(str)) {
            return true;
        }

        return str.isEmpty();
    }

    /**
     * 如果字符串为空，返回默认值
     *
     * @param str 字符串
     * @param d   默认值
     * @return 字符串
     */
    public static String defaultIfEmpty(final String str, final String d) {
        if (isEmpty(str)) {
            return d;
        }

        return str;
    }

    /**
     * 驼峰转下划线（如userName -> user_name）
     *
     * @param str
     * @return str
     */
    public static String toUnderscore(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.length() > 1 ? sb.substring(1) : sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param str
     * @return str
     */
    public static String toCamelCase(String str) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    sb.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 格式化字符串 <br>
     * 转义符可以使用 \\ 转义 <br>
     * 例如：a b \\{\\} d -> a b {} d <br>
     * 例如：a b \\\\{\\\\} d -> a b \{\} d <br>
     *
     * @param str  模板
     * @param args 参数
     * @return 格式化后的字符串
     */
    public static String fmt(final String str, final Object... args) {
        if (str == null || args == null) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str.length() * 2);
        int argIndex = 0;
        int length = str.length();
        int i = 0;

        while (i < length) {
            char c = str.charAt(i);
            if (c == '\\' && i + 1 < length) {
                char c1 = str.charAt(++i);
                if (c1 == '{' || c1 == '}') {
                    sb.append(c1);
                } else {
                    sb.append(c);
                }
                i++;
            } else if (c == '{' && i + 1 < length && str.charAt(i + 1) == '}') {
                sb.append(args[argIndex]);
                argIndex++;
                i += 2;
            } else {
                sb.append(c);
                i++;
            }
        }

        return sb.toString();
    }

    /**
     * 格式化字符串
     * 转义符可以使用 \\ 转义 <br>
     * 例如：a b \\{c\\} d -> a b {c} d <br>
     * 例如：a b \\\\{c\\\\} d -> a b \{c\} d <br>
     *
     * @param str 模板
     * @param map 参数
     * @return 格式化后的字符串
     */
    public static String fmt(final String str, final Map<String, String> map) {
        if (str == null || map == null) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str.length() * 2);
        int length = str.length();
        int i = 0;

        while (i < length) {
            char c = str.charAt(i);
            if (c == '\\' && i + 1 < length) {
                char c1 = str.charAt(++i);
                if (c1 == '{' || c1 == '}') {
                    sb.append(c1);
                } else {
                    sb.append(c);
                }
                i++;
            } else if (c == '{') {
                int end = str.indexOf('}', i + 1);
                if (end == -1) {
                    sb.append(str, i, length);
                    break;
                }
                String key = str.substring(i + 1, end);
                String value = map.getOrDefault(key, "{" + key + "}");
                sb.append(value);
                i = end + 1;
            } else {
                sb.append(c);
                i++;
            }
        }

        return sb.toString();
    }

}
