package io.github.kongweiguang.core.util;

/**
 * 线程工具
 *
 * @author kongweiguang
 */
public class Threads {

    /**
     * 使当前线程睡眠指定的时间
     *
     * @param millis 睡眠时间 单位毫秒
     */
    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {

        }
    }

    /**
     * 使当前线程进入等待状态，线程被阻塞
     *
     * @param obj 指定线程的对象
     */

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static void sync(final Object obj) {
        synchronized (obj) {
            try {
                obj.wait();
            } catch (InterruptedException ignored) {

            }
        }
    }

}
