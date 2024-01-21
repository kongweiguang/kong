package io.github.kongweiguang.core;

import static java.util.Objects.isNull;

/**
 * object工具类
 *
 * @author kongweiguang
 */
public class Objs {

    /**
     * 如果当前对象是空的则返回默认值
     *
     * @param obj 需要判断对象
     * @param def 默认值
     * @param <T> 类型
     * @return 数据
     */
    public static <T> T defaultIfNull(T obj, T def) {
        if (isNull(obj)) {
            return def;
        }

        return obj;
    }
}
