package io.github.kongweiguang.bus.core;

/**
 * 合并器
 *
 * @author kongweiguang
 */
@FunctionalInterface
public interface Merge<Action> {
    /**
     * 名称
     *
     * @return 名称
     */
    default String name() {
        return null;
    }

    /**
     * 合并操作
     *
     * @param action 操作
     * @throws Exception 异常
     */
    void mr(Action action) throws Exception;
}
