package io.github.kongweiguang.core;

/**
 * 断言工具
 *
 * @author kongweiguang
 */
public class Assert {

    /**
     * 判断对象不为空，为空抛出异常
     *
     * @param obj 需要判空的对象
     * @param msg 异常信息
     */
    public static void notNull(final Object obj, final String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * 判断结果是否为ture，不为ture抛出异常
     *
     * @param bool 需要判断的结果
     * @param msg  异常信息
     */
    public static void isTure(final boolean bool, final String msg) {
        if (!bool) {
            throw new IllegalArgumentException(msg);
        }
    }

}
