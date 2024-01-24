package io.github.kongweiguang.core.pattern.chain;

/**
 * 流水线模式
 *
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author kongweiguang
 */
public final class Pipeline<I, O> {
    private final PipeHandler<I, O> currHandler;

    private Pipeline(final PipeHandler<I, O> rootHandler) {
        this.currHandler = rootHandler;
    }

    /**
     * 构造pipeline
     *
     * @param handler 处理器
     * @param <I>     输入的类型
     * @param <O>     输出的类型
     * @return {@link PipeHandler}
     */
    public static <I, O> Pipeline<I, O> of(final PipeHandler<I, O> handler) {
        return new Pipeline<>(handler);
    }

    /**
     * 设置下一个pipeline
     *
     * @param handler 处理器
     * @param <K>     处理器的输出类型
     * @return {@link PipeHandler}
     */
    public <K> Pipeline<I, K> next(final PipeHandler<O, K> handler) {
        return of(input -> handler.handle(currHandler.handle(input)));
    }

    /**
     * 执行pipeline
     *
     * @param input 输入内容
     * @return 处理结果
     */
    public O exec(final I input) {
        return currHandler.handle(input);
    }
}
