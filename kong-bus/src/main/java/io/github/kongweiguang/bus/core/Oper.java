package io.github.kongweiguang.bus.core;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * 操作
 *
 * @param <C> 操作的内容类型
 * @param <R> 合并后返回的结果类型
 * @author kongweiguang
 */
public final class Oper<C, R> {
    private final long id;
    private final String branch;
    private final C content;
    private Consumer<R> call;
    private Map<Object, Object> tag;

    private Oper(final long id, final String branch, final C c) {
        this.id = id;
        this.branch = branch;
        this.content = c;
    }

    public static <C, R> Oper<C, R> of(final String branch, final C c) {
        return of(IdGen.of.next(), branch, c);
    }

    public static <C, R> Oper<C, R> of(final long id, final String branch, final C c) {
        return new Oper<>(id, branch, c);
    }

    /**
     * 获取消息id
     *
     * @return id
     */
    public long id() {
        return id;
    }

    /**
     * 获取操作的分支
     *
     * @return 分支
     */
    public String branch() {
        return branch;
    }

    /**
     * 操作内容
     *
     * @return 内容
     */
    public C content() {
        return content;
    }

    /**
     * 判断是否有回调
     *
     * @return 是否有回调
     */
    public boolean hasCallBack() {
        return nonNull(call);
    }

    /**
     * 触发推送者的回调
     *
     * @param r 回调
     */
    public void res(R r) {
        if (hasCallBack()) {
            call.accept(r);
        }
    }

    /**
     * 获取标签
     *
     * @param k   键
     * @param <T> 返回类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T tag(final String k) {
        if (isNull(tag)) {
            return null;
        }

        return (T) tag.get(k);
    }

    /**
     * 添加标签
     *
     * @param k 键
     * @param v 值
     * @return this
     */
    public Oper<C, R> tag(final Object k, final Object v) {
        if (isNull(tag)) {
            this.tag = new HashMap<>();
        }

        tag.put(k, v);
        return this;
    }

    /**
     * 设置回调函数
     *
     * @param call 回调函数
     */
    void callback(final Consumer<R> call) {
        this.call = call;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Oper.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("branch='" + branch + "'")
                .add("content=" + content)
                .add("tag=" + tag)
                .toString();
    }
}
