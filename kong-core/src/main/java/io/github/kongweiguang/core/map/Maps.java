package io.github.kongweiguang.core.map;

import io.github.kongweiguang.core.convert.Cvts;
import io.github.kongweiguang.core.exception.BeanConversionException;
import io.github.kongweiguang.core.lang.Assert;
import io.github.kongweiguang.core.lang.Strs;
import io.github.kongweiguang.core.reflection.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * map工具类
 *
 * @author kongweiguang
 */
public class Maps {


    /**
     * 将map中的key转换为驼峰命名
     *
     * @param map 原map
     * @return 转换后的map
     */
    public static Map<String, Object> key2CamelCase(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        map.forEach((k, v) -> result.put(Strs.toCamelCase(k), v));
        return result;
    }

    /**
     * 将Map转换为指定类型的对象
     *
     * @param map   数据源Map
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 转换后的对象
     */
    public static <T> T toBean(Map<String, Object> map, Class<T> clazz) {
        Assert.notNull(map, "Map must not be null");
        Assert.notNull(clazz, "Class must not be null");

        try {
            T instance = Reflections.createInstance(clazz);
            return Reflections.populateBean(map, instance);
        } catch (Exception e) {
            throw new BeanConversionException("Failed to convert map to bean of type " + clazz.getName(), e);
        }
    }

    /**
     * 将Map列表转换为指定类型的对象列表
     *
     * @param mapList 数据源Map列表
     * @param clazz   目标类型
     * @param <T>     泛型
     * @return 转换后的对象列表
     */
    public static <T> List<T> toBeanList(List<Map<String, Object>> mapList, Class<T> clazz) {
        Assert.notNull(mapList, "Map list must not be null");
        Assert.notNull(clazz, "Class must not be null");

        List<T> resultList = new ArrayList<>(mapList.size());
        for (Map<String, Object> map : mapList) {
            resultList.add(toBean(map, clazz));
        }
        return resultList;
    }


    /**
     * 将对象转换为Map
     *
     * @param obj 源对象
     * @return 转换后的Map
     */
    public static Map<String, Object> toMap(Object obj) {
        Assert.notNull(obj, "Source object must not be null");

        return Cvts.toMap(obj);
    }

    /**
     * 将对象列表转换为Map列表
     *
     * @param objList 对象列表
     * @return 转换后的Map列表
     */
    public static List<Map<String, Object>> toMapList(List<?> objList) {
        Assert.notNull(objList, "Object list must not be null");

        List<Map<String, Object>> resultList = new ArrayList<>(objList.size());
        for (Object obj : objList) {
            resultList.add(toMap(obj));
        }
        return resultList;
    }

}
