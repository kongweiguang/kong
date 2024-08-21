package io.github.kongweiguang.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.MapperFeature.USE_STD_BEAN_NAMING;
import static com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static io.github.kongweiguang.core.util.Strs.isEmpty;
import static java.util.Objects.isNull;
import static java.util.TimeZone.getTimeZone;


/**
 * jackson序列化、反序列化工具
 *
 * @author kongweiguang
 */
public final class Json {

    private static JsonMapper mapper = JsonMapper.builder()
            //忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            //忽略空Bean转json的错误
            .configure(FAIL_ON_EMPTY_BEANS, false)
            //允许不带引号的字段名称
            .configure(ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true)
            //允许单引号
            .configure(ALLOW_SINGLE_QUOTES.mappedFeature(), true)
            //allow int startWith 0
            .configure(ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true)
            //允许字符串存在转义字符：\r \n \t
            .configure(ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
            //排除空值字段
            .serializationInclusion(NON_NULL)
            //使用驼峰式
            .propertyNamingStrategy(LOWER_CAMEL_CASE)
            //使用bean名称
            .enable(USE_STD_BEAN_NAMING)
            //所有日期格式都统一为固定格式
            .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .defaultTimeZone(getTimeZone("GMT+8"))
            //适配Java8中的时间
            .addModule(new JavaTimeModule())
            .build();

    /**
     * 自定义json转换mapper
     *
     * @param jsonMapper jsonMapper
     */
    public static void mapper(final JsonMapper jsonMapper) {
        Json.mapper = jsonMapper;
    }

    /**
     * 获取jsonMapper
     *
     * @return jsonMapper
     */
    public static JsonMapper mapper() {
        return Json.mapper;
    }

    /**
     * 对象转换为json字符串
     *
     * @param obj 要转换的对象
     * @return json字符串
     */
    public static <T> String toStr(final T obj) {
        return toStr(obj, false);
    }

    /**
     * 对象转换为json字符串
     *
     * @param obj    要转换的对象
     * @param format 是否格式化json
     * @return json字符串
     */
    public static <T> String toStr(final T obj, final boolean format) {
        try {
            if (isNull(obj)) {
                return null;
            }

            if (obj instanceof Number) {
                return obj.toString();
            }

            if (obj instanceof String) {
                return (String) obj;
            }

            if (format) {
                return mapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }

            return mapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转换为指定对象
     *
     * @param json  json字符串
     * @param clazz 目标对象
     * @return 对象
     */
    @SuppressWarnings("all")
    public static <T> T toObj(final String json, final Class<T> clazz) {
        if (isEmpty(json) || isNull(clazz)) {
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) json : mapper().readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 字符串转换为指定对象
     *
     * @param json          json字符串
     * @param typeReference 目标对象类型
     */
    public static <T> T toObj(final String json, final TypeReference<T> typeReference) {
        if (isEmpty(json) || isNull(typeReference)) {
            return null;
        }

        try {
            return mapper().readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转换为指定对象
     *
     * @param json     字符串
     * @param javaType 目标对象类型
     * @return 对象
     */
    public static <T> T toObj(final String json, final JavaType javaType) {
        if (isEmpty(json) || isNull(javaType)) {
            return null;
        }

        try {
            return mapper().readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转换为JsonNode对象
     *
     * @param json json字符串
     */
    public static JsonNode toNode(final String json) {
        if (isEmpty(json)) {
            return mapper().createObjectNode();
        }

        try {
            return mapper().readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对象转换为JsonNode对象
     *
     * @param obj 对象
     * @return jsonNode
     */
    public static JsonNode toNode(final Object obj) {
        if (isNull(obj)) {
            return mapper().createObjectNode();
        }

        try {
            return mapper().valueToTree(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取javaType
     *
     * @param parametrized     泛型
     * @param parameterClasses 泛型参数
     * @return javaType
     */
    public static JavaType javaType(Class<?> parametrized, Class<?>... parameterClasses) {
        return mapper().getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    /**
     * 对象转换为map对象
     *
     * @param json 需要转换的json字符串
     * @param k    健的类型
     * @param v    值的类型
     * @return map
     */
    public static <K, V> Map<K, V> toMap(final String json, final Class<K> k, final Class<V> v) {
        if (isEmpty(json)) {
            return new HashMap<>();
        }

        return toObj(json, javaType(Map.class, k, v));
    }

    /**
     * json字符串转换为map对象
     *
     * @param json          json字符串
     * @param typeReference 目标对象类型
     * @param <K>           健的类型
     * @param <V>           值的类型
     * @return 对象
     */
    public static <K, V> Map<K, V> toMap(final String json, TypeReference<Map<K, V>> typeReference) {
        if (isEmpty(json)) {
            return new HashMap<>();
        }

        return toObj(json, typeReference);
    }

    /**
     * 对象转换为map对象
     *
     * @param json 需要转换的json字符串
     * @return map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(final String json) {
        return (Map<K, V>) toMap(json, Object.class, Object.class);
    }

    /**
     * 对象转换为map对象
     *
     * @param obj 对象
     * @param <K> 健的类型
     * @param <V> 值的类型
     * @return map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(final Object obj) {
        return (Map<K, V>) toMap(obj, Object.class, Object.class);
    }

    /**
     * 对象转换为map对象
     *
     * @param obj 对象
     * @param <K> 健的类型
     * @param <V> 值的类型
     * @return map
     */
    public static <K, V> Map<K, V> toMap(final Object obj, final Class<K> k, final Class<V> v) {
        if (isNull(obj)) {
            return new HashMap<>();
        }

        return mapper().convertValue(obj, javaType(Map.class, k, v));
    }

    /**
     * 对象转换为map对象
     *
     * @param obj           对象
     * @param typeReference 目标对象类型
     * @param <K>           健的类型
     * @param <V>           值的类型
     * @return 对象
     */
    public static <K, V> Map<K, V> toMap(final Object obj, TypeReference<Map<K, V>> typeReference) {
        if (isNull(obj)) {
            return new HashMap<>();
        }

        return mapper().convertValue(obj, typeReference);
    }

    /**
     * json字符串转换为list对象，并指定元素类型
     *
     * @param json  json字符串
     * @param clazz list的元素类型
     * @return list
     */
    public static <T> List<T> toList(final String json, final Class<T> clazz) {
        if (isEmpty(json)) {
            return new ArrayList<>();
        }

        return toObj(json, javaType(List.class, clazz));
    }

    /**
     * 对象转换为list对象，并指定元素类型
     *
     * @param json          对象
     * @param typeReference 目标对象类型
     * @param <T>           元素类型
     * @return 对象
     */
    public static <T> List<T> toList(final String json, TypeReference<List<T>> typeReference) {
        if (isEmpty(json)) {
            return new ArrayList<>();
        }

        return toObj(json, typeReference);
    }

    /**
     * json字符串转换为list对象，并指定元素类型
     *
     * @param json json字符串
     * @return list
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(final String json) {
        return (List<T>) toList(json, Object.class);
    }

    /**
     * 对象转换为list对象，并指定元素类型
     *
     * @param obj   对象
     * @param clazz 元素类型
     * @param <T>   元素类型
     * @return 对象
     */
    public static <T> List<T> toList(final Object obj, final Class<T> clazz) {
        if (isNull(obj)) {
            return new ArrayList<>();
        }

        return mapper().convertValue(obj, javaType(List.class, clazz));
    }

    /**
     * 对象转换为list对象，并指定元素类型
     *
     * @param obj           对象
     * @param typeReference 目标对象类型
     * @param <T>           元素类型
     * @return 对象
     */
    public static <T> List<T> toList(final Object obj, final TypeReference<List<T>> typeReference) {
        if (isNull(obj)) {
            return new ArrayList<>();
        }

        return mapper().convertValue(obj, typeReference);
    }


    /**
     * 对象转换为list对象，并指定元素类型
     *
     * @param obj 对象
     * @param <T> 元素类型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(final Object obj) {
        return (List<T>) toList(obj, Object.class);
    }

    /**
     * 创建json对象
     *
     * @return json对象 {@link JsonObj}
     */
    public static JsonObj obj() {
        return JsonObj.of();
    }

    /**
     * 创建json数组
     *
     * @return json数组 {@link JsonAry}
     */
    public static JsonAry ary() {
        return JsonAry.of();
    }
}

