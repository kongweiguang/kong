package io.github.kongweiguang.core.pattern.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.kongweiguang.core.lang.Assert.isTure;
import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.core.lang.If.trueRun;
import static java.util.Optional.ofNullable;

/**
 * 责任链流程器
 *
 * @param <C> 上下文
 * @author kongweiguang
 */
public final class Chain<C> {
    private final List<ChainHandler<C>> chain = new ArrayList<>();
    private final AtomicInteger index = new AtomicInteger();
    private final AtomicReference<C> context = new AtomicReference<>();

    public Chain(final C c) {
        context.set(c);
    }

    /**
     * 创建一个责任链
     *
     * @param <C> 上下文类型
     * @return {@link ChainHandler}
     */
    public static <C> Chain<C> of(final C c) {
        return new Chain<>(c);
    }

    /**
     * 获取上下文
     *
     * @return 上下文
     */
    public C get() {
        return context.get();
    }

    /**
     * 设置上下文
     *
     * @param c 上下文
     */
    public void set(final C c) {
        context.set(c);
    }

    /**
     * 添加处理器
     *
     * @param handler 处理器
     * @return {@link ChainHandler}
     */
    public Chain<C> add(final ChainHandler<C> handler) {
        notNull(handler, "handler must not be null");

        chain.add(handler);
        return this;
    }

    /**
     * 开始流程
     */
    public void process() {
        if (index.get() >= chain.size()) {
            return;
        }

        ofNullable(chain.get(index.get())).ifPresent(h -> trueRun(h.handler(this), this::next));
    }

    /**
     * 从执行指定的流程
     *
     * @param i 从后续的第几个
     */
    public void indexProcess(final int i) {
        int ix = i;

        if (ix < 0) {
            ix = 0;
        }

        index.set(ix);
        process();
    }

    /**
     * 跳过后续指定数量的处理器
     *
     * @param i 从后续的第几个
     */
    public void skip(final int i) {
        isTure(i >= 0, "skip num must > 0");

        indexProcess(index.addAndGet(i + 1));
    }

    /**
     * 执行下一个处理器
     */
    public void next() {
        indexProcess(index.incrementAndGet());
    }

    /**
     * 结束处理
     */
    public void end() {
        indexProcess(chain.size());
    }
}
