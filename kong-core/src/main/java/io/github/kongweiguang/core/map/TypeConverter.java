package io.github.kongweiguang.core.map;

/**
 * 类型转换器接口
 *
 * @param <T> 目标类型
 */
@FunctionalInterface
public interface TypeConverter<T> {
    T convert(Object value);
}
