package io.github.kongweiguang.json;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * json对象
 *
 * @author kongweiguang
 */
public class JsonObj {
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
     * 添加数据到json对象中
     *
     * @param k 健
     * @param v 值
     * @return {@link  JsonObj}
     */
    public JsonObj put(String k, String v) {
        node.put(k, Json.toStr(v));
        return this;
    }

    /**
     * 添加对象数据到json对象中
     *
     * @param k
     * @param v
     * @return
     */
    public JsonObj put(String k, Object v) {
        node.set(k, Json.toNode(Json.toStr(v)));

        return this;
    }

    public JsonObj putObj(String k, Consumer<JsonObj> con) {
        con.accept(JsonObj.of(node.putObject(k)));
        return this;
    }


    public JsonObj putAry(String k, Consumer<JsonAry> con) {
        con.accept(JsonAry.of(node.putArray(k)));
        return this;
    }

    public JsonObj putAry(String k, Collection<?> coll) {
        node.put(k, Json.toStr(coll));
        return this;
    }

    public String build() {
        return node.toString();
    }

    public <K, V> Map<K, V> toMap() {
        return Json.toMap(build());
    }
}
