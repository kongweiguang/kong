package io.github.kongweiguang.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.kongweiguang.core.exception.KongException;
import io.github.kongweiguang.core.lang.Assert;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.MapperFeature.USE_STD_BEAN_NAMING;
import static com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static java.util.Objects.isNull;
import static java.util.TimeZone.getTimeZone;

/**
 * jackson序列化、反序列化工具。
 *
 * @author kongweiguang
 */
public class Json {

    private static final JsonMapper DEFAULT_MAPPER = JsonMapper.builder()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(FAIL_ON_EMPTY_BEANS, false)
            .configure(ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true)
            .configure(ALLOW_SINGLE_QUOTES.mappedFeature(), true)
            .configure(ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true)
            .configure(ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
            .serializationInclusion(NON_NULL)
            .propertyNamingStrategy(LOWER_CAMEL_CASE)
            .enable(USE_STD_BEAN_NAMING)
            .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .defaultTimeZone(getTimeZone("GMT+8"))
            .addModule(new JavaTimeModule())
            .build();

    private static volatile JsonMapper mapper = DEFAULT_MAPPER;

    private Json() {
    }

    /**
     * 自定义全局json mapper。
     *
     * @param jsonMapper mapper
     */
    public static void mapper(JsonMapper jsonMapper) {
        setMapper(jsonMapper);
    }

    /**
     * 自定义全局json mapper。
     *
     * @param jsonMapper mapper
     */
    public static void setMapper(JsonMapper jsonMapper) {
        Assert.notNull(jsonMapper, "jsonMapper must not be null");
        mapper = jsonMapper;
    }

    /**
     * 获取当前使用的mapper。
     *
     * @return current mapper
     */
    public static JsonMapper mapper() {
        return mapper;
    }

    /**
     * 获取默认mapper。
     *
     * @return default mapper
     */
    public static JsonMapper defaultMapper() {
        return DEFAULT_MAPPER;
    }

    /**
     * 对象转换为json字符串。
     *
     * @param obj source object
     * @return json string
     */
    public static <T> String toStr(T obj) {
        return toStr(obj, false);
    }

    /**
     * 对象转换为json字符串。
     *
     * @param obj source object
     * @param format whether pretty print
     * @return json string
     */
    public static <T> String toStr(T obj, boolean format) {
        if (isNull(obj)) {
            return null;
        }

        // Keep raw strings untouched so callers can distinguish "already text/json"
        // from "serialize this object with Jackson".
        if (obj instanceof String str) {
            return str;
        }

        try {
            if (format) {
                return mapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }

            return mapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw wrap("Failed to convert object to json string", e);
        }
    }

    /**
     * 转换为指定对象类型。
     *
     * @param json source value
     * @param clazz target class
     * @return target object
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObj(Object json, Class<T> clazz) {
        if (json == null || clazz == null) {
            return null;
        }

        if (clazz.equals(String.class) && json instanceof String str) {
            return (T) str;
        }

        try {
            if (json instanceof String str) {
                return mapper().readValue(str, clazz);
            }

            return mapper().convertValue(json, clazz);
        } catch (IOException | IllegalArgumentException e) {
            throw wrap("Failed to convert value to " + clazz.getName(), e);
        }
    }

    /**
     * 转换为指定泛型对象类型。
     *
     * @param json source value
     * @param typeReference target type
     * @return target object
     */
    public static <T> T toObj(Object json, TypeReference<T> typeReference) {
        if (json == null || typeReference == null) {
            return null;
        }

        try {
            if (json instanceof String str) {
                return mapper().readValue(str, typeReference);
            }

            return mapper().convertValue(json, typeReference);
        } catch (IOException | IllegalArgumentException e) {
            throw wrap("Failed to convert value by type reference", e);
        }
    }

    /**
     * 转换为指定javaType。
     *
     * @param json source value
     * @param javaType target type
     * @return target object
     */
    public static <T> T toObj(Object json, JavaType javaType) {
        if (json == null || javaType == null) {
            return null;
        }

        try {
            if (json instanceof String str) {
                return mapper().readValue(str, javaType);
            }

            return mapper().convertValue(json, javaType);
        } catch (IOException | IllegalArgumentException e) {
            throw wrap("Failed to convert value by java type", e);
        }
    }

    /**
     * 对象转换为{@link JsonNode}。
     *
     * @param obj source object
     * @return json node
     */
    public static JsonNode toNode(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof JsonNode jsonNode) {
            return jsonNode;
        }

        try {
            if (obj instanceof String str) {
                return mapper().readTree(str);
            }

            return mapper().valueToTree(obj);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw wrap("Failed to convert value to JsonNode", e);
        }
    }

    /**
     * 获取泛型javaType。
     *
     * @param parametrized raw type
     * @param parameterClasses parameter types
     * @return java type
     */
    public static JavaType javaType(Class<?> parametrized, Class<?>... parameterClasses) {
        return mapper().getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    /**
     * 转换为map。
     *
     * @param obj source object
     * @param keyClass key type
     * @param valueClass value type
     * @return converted map
     */
    public static <K, V> Map<K, V> toMap(Object obj, Class<K> keyClass, Class<V> valueClass) {
        if (obj == null) {
            return null;
        }

        return toObj(obj, javaType(Map.class, keyClass, valueClass));
    }

    /**
     * 转换为map。
     *
     * @param obj source object
     * @param typeReference target type
     * @return converted map
     */
    public static <K, V> Map<K, V> toMap(Object obj, TypeReference<Map<K, V>> typeReference) {
        return toObj(obj, typeReference);
    }

    /**
     * 转换为list。
     *
     * @param obj source object
     * @param clazz element type
     * @return converted list
     */
    public static <T> List<T> toList(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }

        return toObj(obj, javaType(List.class, clazz));
    }

    /**
     * 转换为list。
     *
     * @param obj source object
     * @param typeRef target type
     * @return converted list
     */
    public static <T> List<T> toList(Object obj, TypeReference<List<T>> typeRef) {
        return toObj(obj, typeRef);
    }

    /**
     * 创建json object builder。
     *
     * @return json object builder
     */
    public static JsonObj obj() {
        return JsonObj.of();
    }

    /**
     * 创建json array builder。
     *
     * @return json array builder
     */
    public static JsonAry ary() {
        return JsonAry.of();
    }

    /**
     * 读取json文件。
     *
     * @param path file path
     * @param charset charset
     * @return parsed node
     */
    public static JsonNode read(Path path, Charset charset) {
        Assert.notNull(path, "path must not be null");
        Assert.notNull(charset, "charset must not be null");

        try {
            return toNode(Files.readString(path, charset));
        } catch (IOException e) {
            throw wrap("Failed to read json file: " + path, e);
        }
    }

    /**
     * 使用utf-8读取json文件。
     *
     * @param path file path
     * @return parsed node
     */
    public static JsonNode read(Path path) {
        return read(path, StandardCharsets.UTF_8);
    }

    /**
     * 读取jsonl文件。
     *
     * @param path file path
     * @param charset charset
     * @return parsed node list
     */
    public static List<JsonNode> readJsonl(Path path, Charset charset) {
        Assert.notNull(path, "path must not be null");
        Assert.notNull(charset, "charset must not be null");

        try {
            return Files.readAllLines(path, charset)
                    .stream()
                    // Tolerate trailing blank lines in jsonl files.
                    .filter(line -> !line.isBlank())
                    .map(Json::toNode)
                    .toList();
        } catch (IOException e) {
            throw wrap("Failed to read jsonl file: " + path, e);
        }
    }

    /**
     * 使用utf-8读取jsonl文件。
     *
     * @param path file path
     * @return parsed node list
     */
    public static List<JsonNode> readJsonl(Path path) {
        return readJsonl(path, StandardCharsets.UTF_8);
    }

    private static KongException wrap(String message, Exception e) {
        return new KongException(message, e);
    }
}
