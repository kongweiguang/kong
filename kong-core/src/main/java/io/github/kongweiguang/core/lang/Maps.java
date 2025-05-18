package io.github.kongweiguang.core.lang;

import java.util.HashMap;
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
}
