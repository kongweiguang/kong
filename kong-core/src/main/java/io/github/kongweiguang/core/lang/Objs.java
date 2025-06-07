package io.github.kongweiguang.core.lang;

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

    /**
     * 判断对象是否为空
     *
     * @param obj 需要判断对象
     * @return 是否为空
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断对象是否不为空
     *
     * @param obj 需要判断对象
     * @return 是否不为空
     */
    public static boolean notNull(Object obj) {
        return !isNull(obj);
    }
}
