package io.github.kongweiguang.core;

import java.util.function.Supplier;

/**
 * 简化if-else
 *
 * @author kongweiguang
 */
public class If {

    /**
     * 条件为ture则执行
     *
     * @param bool 条件
     * @param r    执行方法
     */
    public static void trueRun(boolean bool, Runnable r) {
        if (bool) {
            r.run();
        }
    }

    /**
     * 条件为ture则返回f1,否则返回f2
     *
     * @param bool 条件
     * @param f1   方法1
     * @param f2   方法2
     * @param <T>  返回类型
     * @return 返回值
     */
    public static <T> T trueRunF1(final boolean bool, Supplier<T> f1, Supplier<T> f2) {
        if (bool) {
            return f1.get();
        }

        return f2.get();
    }
}
