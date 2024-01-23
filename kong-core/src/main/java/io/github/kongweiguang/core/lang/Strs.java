package io.github.kongweiguang.core.lang;

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

}
