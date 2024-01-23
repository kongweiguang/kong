package io.github.kongweiguang.core.lang;

import java.util.function.Function;
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
     * @param <T>  返回值类型
     * @return 结果
     */
    public static <T> T trueSup(boolean bool, Supplier<T> r) {
        if (bool) {
            return r.get();
        }

        return null;
    }

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
    public static <T> T trueSupF1(final boolean bool, Supplier<T> f1, Supplier<T> f2) {
        if (bool) {
            return f1.get();
        }

        return f2.get();
    }

    /**
     * 条件为ture则返回f1,否则返回f2
     *
     * @param bool 条件
     * @param f1   方法1
     * @param f2   方法2
     */
    public static void trueRunF1(final boolean bool, Runnable f1, Runnable f2) {
        if (bool) {
            f1.run();
        }

        f2.run();
    }

    /**
     * 执行方法，出现异常执行第二个方法
     *
     * @param sup1 执行的方法
     * @param sup2 兜底方法
     * @param <T>  返回的结果类型
     * @return 方法执行后的结果
     */
    public static <T> T errorSupF2(Supplier<T> sup1, Supplier<T> sup2) {
        try {
            return sup1.get();
        } catch (Exception e) {
            return sup2.get();
        }
    }

    /**
     * 执行方法，出现异常执行兜底方法
     *
     * @param sup     执行的方法
     * @param handler 兜底方法
     * @param <T>     返回的结果类型
     * @return 方法执行后的结果
     */
    public static <T> T errorHandler(Supplier<T> sup, Function<Throwable, T> handler) {
        try {
            return sup.get();
        } catch (Exception e) {
            return handler.apply(e);
        }
    }


    /**
     * 获取第一个不为空的方法执行结果
     *
     * @param sups 方法集合
     * @param <T>  返回结果的类型
     * @return 方法执行的结果
     */
    @SafeVarargs
    public static <T> T firstNonNull(Supplier<T>... sups) {
        return firstNonNull(false, sups);
    }

    /**
     * 获取第一个不为空的方法执行结果，当出现异常时，根据 continueOnException 参数决定是否继续执行
     *
     * @param continueOnException 异常不退出继续执行，true 继续执行，false不执行，抛出异常
     * @param sups                方法集合
     * @param <T>                 返回结果的类型
     * @return 方法执行的结果
     */
    @SafeVarargs
    public static <T> T firstNonNull(boolean continueOnException, Supplier<T>... sups) {
        for (Supplier<T> sup : sups) {
            T t = null;

            try {
                t = sup.get();
            } catch (Exception e) {
                if (!continueOnException) {
                    throw e;
                }
            }

            if (t != null) {
                return t;
            }
        }
        return null;
    }
}
