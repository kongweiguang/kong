package io.github.kongweiguang.json;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.function.Consumer;

import static io.github.kongweiguang.json.Json.toNode;
import static io.github.kongweiguang.json.Json.toStr;
import static java.util.Optional.ofNullable;

/**
 * json对象
 *
 * @author kongweiguang
 */
public final class JsonObj {
    private ObjectNode node = Json.mapper().createObjectNode();

    private JsonObj() {
    }

    private JsonObj(final ObjectNode node) {
        this.node = node;
    }

    /**
     * 工厂方法，创建JsonObj
     *
     * @return {@link  JsonObj}
     */
    public static JsonObj of() {
        return new JsonObj();
    }

    /**
     * 工厂方法，创建sonObj
     *
     * @param node {@link  ObjectNode}
     * @return {@link  JsonObj}
     */
    public static JsonObj of(final ObjectNode node) {
        return new JsonObj(node);
    }


    /**
     * 添加基本类型和字符串数据到json对象中，如果传入对象会转成字符串存储
     *
     * @param k 键
     * @param v 值
     * @return {@link  JsonObj}
     */
    public JsonObj put(final String k, final Object v) {

        node.put(k, toStr(v));

        return this;
    }

    /**
     * 添加对象数据到json对象中
     *
     * @param k 键
     * @param v 值
     * @return {@link  JsonObj}
     */
    public JsonObj putObj(final String k, final Object v) {

        node.set(k, toNode(toStr(v)));

        return this;
    }

    /**
     * 在json对象中添加一个{@link JsonObj}
     *
     * @param k   键的名字
     * @param con {@link  JsonObj} 的构建器
     * @return {@link  JsonObj}
     */
    public JsonObj putObj(final String k, final Consumer<JsonObj> con) {

        con.accept(JsonObj.of(node.putObject(k)));

        return this;
    }

    /**
     * 在json对象中添加一个{@link JsonAry}
     *
     * @param k   键的名字
     * @param con {@link  JsonAry} 的构建器
     * @return {@link  JsonObj}
     */
    public JsonObj putAry(final String k, final Consumer<JsonAry> con) {

        con.accept(JsonAry.of(node.putArray(k)));

        return this;
    }

    /**
     * 将map添加到json对象中
     *
     * @param map 数据
     * @return {@link  JsonObj}
     */
    public JsonObj putMap(final Map<String, Object> map) {

        ofNullable(map).ifPresent(m -> m.forEach(this::putObj));

        return this;
    }

    /**
     * 构建成json对象
     *
     * @return json对象
     */
    public String toJson() {
        return node.toString();
    }

    /**
     * 构建成json对象，格式化
     *
     * @return json对象
     */
    public String toPrettyJson() {
        return node.toPrettyString();
    }

    /**
     * 将json对象转成map
     *
     * @param <K> 键的类型
     * @param <V> 值的类型
     * @return map
     */
    public <K, V> Map<K, V> toMap() {
        return Json.toMap(toJson());
    }
}
