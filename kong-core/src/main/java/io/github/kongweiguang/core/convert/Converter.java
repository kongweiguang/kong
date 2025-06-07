package io.github.kongweiguang.core.convert;

/**
 * 类型转换器接口
 *
 * @param <T> 目标类型
 */
@FunctionalInterface
public interface Converter<T> {
    T convert(Object value);
}