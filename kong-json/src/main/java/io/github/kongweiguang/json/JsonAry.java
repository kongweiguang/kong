package io.github.kongweiguang.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.kongweiguang.core.lang.Assert;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Opt.ofNullable;

/**
 * 链式构建json array。
 *
 * @author kongweiguang
 */
public class JsonAry {
    private final ArrayNode node;

    private JsonAry() {
        this(Json.mapper().createArrayNode());
    }

    private JsonAry(ArrayNode node) {
        Assert.notNull(node, "node must not be null");
        this.node = node;
    }

    /**
     * 创建一个空json array。
     *
     * @return json array builder
     */
    public static JsonAry of() {
        return new JsonAry();
    }

    /**
     * 基于已有{@link ArrayNode}包装json array。
     *
     * @param node array node
     * @return json array builder
     */
    public static JsonAry of(ArrayNode node) {
        return new JsonAry(node);
    }

    /**
     * 添加一个普通json值，按真实json类型保留数值、布尔、null和嵌套结构。
     *
     * @param value array value
     * @return current builder
     */
    public JsonAry add(Object value) {
        JsonValueHelper.addValue(node, value);
        return this;
    }

    /**
     * 显式按字符串添加值。
     *
     * @param value array value
     * @return current builder
     */
    public JsonAry addString(Object value) {
        JsonValueHelper.addString(node, value);
        return this;
    }

    /**
     * 添加一个对象值。
     *
     * @param value object value
     * @return current builder
     */
    public JsonAry addObj(Object value) {
        return add(value);
    }

    /**
     * 添加一个json node。
     *
     * @param nodeValue json node
     * @return current builder
     */
    public JsonAry addObj(JsonNode nodeValue) {
        return add(nodeValue);
    }

    /**
     * 添加一个嵌套json object。
     *
     * @param consumer nested object builder
     * @return current builder
     */
    public JsonAry addObj(Consumer<JsonObj> consumer) {
        Assert.notNull(consumer, "consumer must not be null");
        JsonObj jsonObj = JsonObj.of(node.objectNode());
        consumer.accept(jsonObj);
        return addObj(jsonObj.toNode());
    }

    /**
     * 添加一个嵌套json array。
     *
     * @param consumer nested array builder
     * @return current builder
     */
    public JsonAry addAry(Consumer<JsonAry> consumer) {
        Assert.notNull(consumer, "consumer must not be null");
        JsonAry jsonAry = JsonAry.of(node.arrayNode());
        consumer.accept(jsonAry);
        return add(jsonAry.toNode());
    }

    /**
     * 追加集合中的每个元素。
     *
     * @param collection value collection
     * @return current builder
     */
    public JsonAry addColl(Collection<?> collection) {
        ofNullable(collection).ifPresent(values -> values.forEach(this::add));
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
     * 转换为list。
     *
     * @param clazz element type
     * @return converted list
     */
    public <T> List<T> toList(Class<T> clazz) {
        return Json.toList(node, clazz);
    }

    /**
     * 返回底层{@link JsonNode}。
     *
     * @return current node
     */
    public ArrayNode toNode() {
        return node;
    }
}
