package io.github.kongweiguang.core.pattern.pipe;

/**
 * 流水线的处理器
 *
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author kongweiguang
 */
@FunctionalInterface
public interface PipeHandler<I, O> {
    O handle(I input);
}
