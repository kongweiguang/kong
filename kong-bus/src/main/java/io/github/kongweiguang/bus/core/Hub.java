package io.github.kongweiguang.bus.core;

import java.util.function.Consumer;

/**
 * hub
 *
 * @param <C> 操作类型
 * @param <R> 返回类型
 * @author kongweiguang
 */
public interface Hub<C, R> {

    /**
     * 推送实体类操作
     *
     * @param c 内容
     */
    default Hub<C, R> push(final C c) {
        return push(Operation.of(c.getClass().getName(), c), null);
    }

    /**
     * 推送实体操作，设置回调
     *
     * @param c    内容
     * @param call 回调
     */
    default Hub<C, R> push(final C c, final Consumer<R> call) {
        return push(Operation.of(c.getClass().getName(), c), call);
    }

    /**
     * 推送操作
     *
     * @param branch 分支
     * @param c      内容
     */
    default Hub<C, R> push(final String branch, final C c) {
        return push(Operation.of(branch, c), null);
    }

    /**
     * 推送操作，设置回调
     *
     * @param branch 分支
     * @param c      内容
     * @param call   回调
     */
    default Hub<C, R> push(final String branch, final C c, final Consumer<R> call) {
        return push(Operation.of(branch, c), call);
    }

    /**
     * 推送操作
     *
     * @param operation 操作
     */
    default Hub<C, R> push(final Operation<C, R> operation) {
        return push(operation, null);
    }

    /**
     * 推送操作，设置回调
     *
     * @param operation 操作
     * @param call      回调
     */
    Hub<C, R> push(final Operation<C, R> operation, final Consumer<R> call);

    /**
     * 拉取指定的实体类型的操作
     *
     * @param clazz 实体类型
     * @param merge 合并器
     */
    default Hub<C, R> pull(final Class<?> clazz, final Merge<Operation<C, R>> merge) {
        return pull(clazz.getName(), merge);
    }

    /**
     * 拉取指定的实体类型的操作
     *
     * @param clazz 实体类型
     * @param index 拉取的顺序
     * @param merge 合并器
     */
    default Hub<C, R> pull(final Class<?> clazz, final int index, final Merge<Operation<C, R>> merge) {
        return pull(clazz.getName(), index, merge);
    }

    /**
     * 拉取指定的branch
     *
     * @param branch 分支
     * @param merge  合并器
     */
    default Hub<C, R> pull(final String branch, final Merge<Operation<C, R>> merge) {
        return pull(branch, 0, merge);
    }

    /**
     * 拉取指定的branch
     *
     * @param branch 分支
     * @param index  拉取的顺序
     * @param merge  合并器
     */
    Hub<C, R> pull(final String branch, final int index, final Merge<Operation<C, R>> merge);

    /**
     * 拉取类中加了{@link Pull}注解的方法，并在推送到指定的分支合并
     *
     * @param obj 含有{@link Pull}注解的类实例
     */
    Hub<C, R> pullClass(final Object obj);

    /**
     * 移除branch下指定名称的合并器
     *
     * @param branch 分支
     * @param name   合并器的名字
     */
    Hub<C, R> remove(final String branch, final String name);


    /**
     * 移除类中加了{@link Pull}注解的方法，并在推送到指定的分支合并
     *
     * @param obj 含有{@link Pull}注解的类实例
     */
    Hub<C, R> removeClass(final Object obj);

}
