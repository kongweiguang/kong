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

    public static void copy(final Object source, final Class<?> target) {
        try {
            copy(source, target.newInstance(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void copy(final Object source, final Object target) {
        copy(source, target, null);
    }

    public static void copy(final Object source, final Object target, Converter converter) {
        final boolean hasConverter = nonNull(converter);
        cache.computeIfAbsent(key(source.getClass(), target.getClass(), hasConverter),
                        k -> BeanCopier.create(source.getClass(), target.getClass(), hasConverter))
                .copy(source, target, converter);
    }

    private static String key(final Class<?> source, final Class<?> target, boolean hasConverter) {
        return String.join("_", source.getName(), target.getName(), String.valueOf(hasConverter));
    }

    public static BeanMap toMap(final Object obj) {
        return BeanMap.create(obj);
    }

    @SuppressWarnings("rawtypes")
    public static <T> T toObj(final Map map, final T bean) {
        BeanMap.create(bean).putAll(map);
        return bean;
    }

    @SuppressWarnings("rawtypes")
    public static <T> T toObj(final Map map, final Class<T> beanClass) {
        try {
            return toObj(map, beanClass.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
