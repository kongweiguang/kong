package io.github.kongweiguang.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.kongweiguang.core.lang.Assert;

import java.util.Map;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Opt.ofNullable;

/**
 * 链式构建json object。
 *
 * @author kongweiguang
 */
public class JsonObj {
    private final ObjectNode node;

    private JsonObj() {
        this(Json.mapper().createObjectNode());
    }

    private JsonObj(ObjectNode node) {
        Assert.notNull(node, "node must not be null");
        this.node = node;
    }

    /**
     * 创建一个空json object。
     *
     * @return json object builder
     */
    public static JsonObj of() {
        return new JsonObj();
    }

    /**
     * 基于已有{@link ObjectNode}包装json object。
     *
     * @param node object node
     * @return json object builder
     */
    public static JsonObj of(ObjectNode node) {
        return new JsonObj(node);
    }

    /**
     * 写入一个普通json值，按真实json类型保留数值、布尔、null和嵌套结构。
     *
     * @param key field name
     * @param value field value
     * @return current builder
     */
    public JsonObj put(String key, Object value) {
        Assert.notNull(key, "key must not be null");
        JsonValueHelper.putValue(node, key, value);
        return this;
    }

    /**
     * 显式按字符串写入值。
     *
     * @param key field name
     * @param value field value
     * @return current builder
     */
    public JsonObj putString(String key, Object value) {
        Assert.notNull(key, "key must not be null");
        JsonValueHelper.putString(node, key, value);
        return this;
    }

    /**
     * 写入一个对象值。
     *
     * @param key field name
     * @param value object value
     * @return current builder
     */
    public JsonObj putObj(String key, Object value) {
        return put(key, value);
    }

    /**
     * 写入一个嵌套json object。
     *
     * @param key field name
     * @param consumer nested object builder
     * @return current builder
     */
    public JsonObj putObj(String key, Consumer<JsonObj> consumer) {
        Assert.notNull(key, "key must not be null");
        Assert.notNull(consumer, "consumer must not be null");
        consumer.accept(JsonObj.of(node.putObject(key)));
        return this;
    }

    /**
     * 写入一个嵌套json array。
     *
     * @param key field name
     * @param consumer nested array builder
     * @return current builder
     */
    public JsonObj putAry(String key, Consumer<JsonAry> consumer) {
        Assert.notNull(key, "key must not be null");
        Assert.notNull(consumer, "consumer must not be null");
        consumer.accept(JsonAry.of(node.putArray(key)));
        return this;
    }

    /**
     * 批量写入map中的值。
     *
     * @param map value map
     * @return current builder
     */
    public JsonObj putMap(Map<String, Object> map) {
        ofNullable(map).ifPresent(values -> values.forEach(this::put));
        return this;
    }

    /**
     * 转换为json字符串。
     *
     * @return compact json string
     */
    public String toJson() {
        return node.toString();
    }

    /**
     * 转换为格式化后的json字符串。
     *
     * @return pretty json string
     */
    public String toPrettyJson() {
        return node.toPrettyString();
    }

    /**
     * 转换为map。
     *
     * @param keyClass key type
     * @param valueClass value type
     * @return converted map
     */
    public <K, V> Map<K, V> toMap(Class<K> keyClass, Class<V> valueClass) {
        return Json.toMap(node, keyClass, valueClass);
    }

    /**
     * 返回底层{@link JsonNode}。
     *
     * @return current node
     */
    public ObjectNode toNode() {
        return node;
    }
}
