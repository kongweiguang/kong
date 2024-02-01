package io.github.kongweiguang.core.pattern.chain;

/**
 * 责任链处理器
 *
 * @param <C> 上下文
 * @author kongweiguang
 */
@FunctionalInterface
public interface ChainHandler<C> {
    /**
     * 处理流程
     *
     * @param chain 责任链流程器
     * @return 断言，true继续往下执行，false则不执行后续的处理器
     */
    boolean handler(final Chain<C> chain);
}
