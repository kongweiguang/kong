package io.github.kongweiguang.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * map工具类
 */
public class Maps {


    /**
     * 将map中的key转换为驼峰命名
     *
     * @param map 原map
     * @return 转换后的map
     */
    public static Map<?, ?> key2CamelCase(Map<?, ?> map) {
        Map<String, Object> result = new HashMap<>();
        map.forEach((k, v) -> {
            result.put(Strs.toCamelCase(k.toString()), v);
        });
        return result;
    }
}
