package io.github.kongweiguang.core.reflection;

import io.github.kongweiguang.core.convert.Cvts;
import io.github.kongweiguang.core.exception.BeanConversionException;
import io.github.kongweiguang.core.lang.Strs;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类
 *
 * @author kongweiguang
 */
public class Reflections {
    /**
     * 创建实例
     *
     * @param clazz 类型
     * @param <T>   泛型
     * @return 实例
     * @throws Exception 异常
     */
    public static <T> T createInstance(Class<T> clazz) throws Exception {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new BeanConversionException("No default constructor found for " + clazz.getName(), e);
        }
    }

    /**
     * 设置字段值
     *
     * @param instance 实例
     * @param field    字段
     * @param value    值
     * @throws Exception 异常
     */
    public static void setFieldValue(Object instance, Field field, Object value) throws Exception {
        Class<?> fieldType = field.getType();

        // 如果值为null或者类型已经匹配，直接设置
        if (value == null || fieldType.isInstance(value)) {
            field.setAccessible(true);
            field.set(instance, value);
            return;
        }

        // 尝试使用setter方法
        String setterName = "set" + Strs.capitalize(field.getName());
        try {
            Method setter = instance.getClass().getMethod(setterName, fieldType);
            setter.invoke(instance, Cvts.convert(value, fieldType));
            return;
        } catch (NoSuchMethodException e) {
            // 没有setter方法，继续使用反射设置字段
        }

        // 使用类型转换器转换值
        field.setAccessible(true);
        field.set(instance, Cvts.convert(value, fieldType));
    }


    /**
     * 填充Bean属性
     *
     * @param map      数据源Map
     * @param instance 实例
     * @param <T>      泛型
     * @return 填充后的实例
     * @throws Exception 异常
     */
    public static <T> T populateBean(Map<String, Object> map, T instance) throws Exception {
        Class<?> clazz = instance.getClass();
        Map<String, Field> fieldMap = getAllFields(clazz);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            // 尝试直接匹配字段名
            Field field = fieldMap.get(key);

            // 尝试驼峰命名转换（如 user_name -> userName）
            if (field == null && key.contains("_")) {
                String camelKey = Strs.toCamelCase(key);
                field = fieldMap.get(camelKey);
            }

            if (field != null) {
                setFieldValue(instance, field, value);
            }
        }

        return instance;
    }

    /**
     * 获取类的所有字段（包括父类）
     *
     * @param clazz 类
     * @return 字段Map
     */
    private static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                fieldMap.putIfAbsent(field.getName(), field);
            }
            currentClass = currentClass.getSuperclass();
        }

        return fieldMap;
    }


    /**
     * 获取方法的参数泛型
     *
     * @param m 方法
     * @return 参数泛型列表
     */
    public static List<String> generics(Method m) {
        List<String> fr = new ArrayList<>(2);
        Type[] genericParameterTypes = m.getGenericParameterTypes();

        for (Type type : genericParameterTypes) {
            if (type instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();

                for (Type actualType : actualTypeArguments) {
                    fr.add(actualType.getTypeName());
                }

            }
        }

        return fr;
    }
}
