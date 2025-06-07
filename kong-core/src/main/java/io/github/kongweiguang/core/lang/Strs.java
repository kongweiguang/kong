package io.github.kongweiguang.core.lang;

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
    public static boolean isEmpty(String str) {
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
    public static String defaultIfEmpty(String str, String d) {
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
     * 格式化字符串 </br>
     * 例如：</br>
     * String template = "Hello {}, this is {} test\\{\\}  \\123 {}"; </br>
     * 输出：Hello World, this is performance test{}  \123 {} </br>
     * 转义符可以使用 \\ 转义 </br>
     * 例如：</br>
     * a b \\{\\} d -> a b {} d </br>
     * a b \\\\{\\\\} d -> a b \{\} d </br>
     *
     * @param str  模板字符串
     * @param args 参数列表
     * @return 格式化后的字符串
     */
    public static String fmt(String str, Object... args) {
        if (str == null || args == null || args.length == 0) {
            return str;
        }

        char[] chars = str.toCharArray();
        int len = chars.length;
        StringBuilder sb = new StringBuilder(len * 2);
        int argIndex = 0;

        for (int i = 0; i < len; i++) {
            if (i < len - 1) {
                char c = chars[i];
                char next = chars[i + 1];
                if (c == '\\') {
                    if (next == '{' || next == '}') {
                        sb.append(next);
                        i++;
                        continue;
                    }
                } else if (c == '{' && next == '}') {
                    if (argIndex < args.length) {
                        sb.append(args[argIndex]);
                    } else {
                        sb.append("{}");
                    }
                    argIndex++;
                    i++;
                    continue;
                }
            }
            sb.append(chars[i]);
        }
        return sb.toString();
    }


    /**
     * 格式化字符串
     * 将字符串中{key}与map中对应key相同的值替换，找不到对应的值保留原样 </br>
     * 例如：</br>
     * Map<String, String> map = new HashMap<>();</br>
     * map.put("name", "Alice");</br>
     * map.put("age", "30");</br>
     * String template2 = "User {name} is {age} years old \\{meta\\}  \\123 {demo} "; </br>
     * 输出：User Alice is 30 years old {meta}   \123 {demo} </br>
     * 转义符可以使用 \ 转义 </br>
     * 例如：</br>
     * a b \{c\} d -> a b {c} d </br>
     * a b \\{c\\} d -> a b \{c\} d </br>
     *
     * @param str 模板字符串
     * @param map 参数映射
     * @return 格式化后的字符串
     */
    public static String fmt(String str, Map<String, String> map) {
        if (str == null || map == null) {
            return str;
        }

        char[] chars = str.toCharArray();
        int len = chars.length;
        StringBuilder sb = new StringBuilder(len * 2);

        for (int i = 0; i < len; i++) {
            char c = chars[i];
            if (c == '\\' && i + 1 < len) {
                char next = chars[i + 1];
                if (next == '{' || next == '}') {
                    sb.append(next);
                    i++;
                    continue;
                } else {
                    sb.append(c);
                    continue;
                }
            }

            if (c == '{') {
                int j = i + 1;
                while (j < len && chars[j] != '}') {
                    j++;
                }
                if (j == len) {
                    sb.append(str, i, len - i);
                    break;
                }
                String key = new String(chars, i + 1, j - i - 1);
                String replacement = map.get(key);
                if (replacement != null) {
                    sb.append(replacement);
                } else {
                    sb.append('{').append(key).append('}');
                }
                i = j;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 首字母大写
     *
     * @param str 字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

}
