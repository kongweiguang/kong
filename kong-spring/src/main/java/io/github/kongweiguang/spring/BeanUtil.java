package io.github.kongweiguang.spring;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.Converter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

/**
 * bean拷贝
 *
 * @author kongweiguang
 */
public class BeanUtil {
    private static final Map<String, BeanCopier> cache = new ConcurrentHashMap<>();

    /**
     * 将对象拷贝到新的bean上
     *
     * @param source 源对象
     * @param target 目标对象类型
     * @param <T>    目标类型
     * @return 目标对象
     */
    public static <T> T copy(final Object source, final Class<T> target) {
        try {
            return copy(source, target.newInstance(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将源对象拷贝到目标对象
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <T>    目标类型
     * @return 目标对象
     */
    public static <T> T copy(final Object source, final Object target) {
        return copy(source, target, null);
    }

    /**
     * 将源对象拷贝到新对象并自定义转换器
     *
     * @param source    源对象
     * @param target    目标对象
     * @param converter 转换器
     * @param <T>       目标类型
     * @return 目标对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T copy(final Object source, final Object target, Converter converter) {
        final boolean hasConverter = nonNull(converter);
        cache.computeIfAbsent(key(source.getClass(), target.getClass(), hasConverter),
                        k -> BeanCopier.create(source.getClass(), target.getClass(), hasConverter))
                .copy(source, target, converter);
        return (T) target;
    }

    /**
     * 获取拷贝器的健
     *
     * @param source       源对象
     * @param target       目标对象
     * @param hasConverter 是否有转换器
     * @return 健
     */
    private static String key(final Class<?> source, final Class<?> target, boolean hasConverter) {
        return String.join("_", source.getName(), target.getName(), String.valueOf(hasConverter));
    }

    /**
     * 将对象转成map
     *
     * @param obj 源对象
     * @return map
     */
    public static BeanMap toMap(final Object obj) {
        return BeanMap.create(obj);
    }

    /**
     * 将map转成对象
     *
     * @param map  map
     * @param bean 目标对象
     * @param <T>  目标对象类型
     * @return 目标对象
     */
    @SuppressWarnings("rawtypes")
    public static <T> T toObj(final Map map, final T bean) {
        BeanMap.create(bean).putAll(map);
        return bean;
    }

    /**
     * 将map转成对象
     *
     * @param map       map
     * @param beanClass 目标对象的类型
     * @param <T>       目标类型
     * @return 目标对象
     */
    @SuppressWarnings("rawtypes")
    public static <T> T toObj(final Map map, final Class<T> beanClass) {
        try {
            return toObj(map, beanClass.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
