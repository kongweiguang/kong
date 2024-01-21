package io.github.kongweiguang.json;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static io.github.kongweiguang.json.Json.toNode;
import static io.github.kongweiguang.json.Json.toStr;
import static java.util.Optional.ofNullable;

/**
 * json数组
 *
 * @author kongweiguang
 */
public final class JsonAry {
    private ArrayNode node = Json.mapper().createArrayNode();

    private JsonAry() {
    }

    private JsonAry(final ArrayNode node) {
        this.node = node;
    }

    /**
     * 工厂方法，创建JsonAry
     *
     * @return {@link JsonAry}
     */
    public static JsonAry of() {
        return new JsonAry();
    }

    /**
     * 工厂方法，创建JsonAry
     *
     * @param node {@link ArrayNode}
     * @return {@link JsonAry}
     */
    public static JsonAry of(final ArrayNode node) {
        return new JsonAry(node);
    }

    /**
     * 添加基本类型或者字符串，如果传入对象会转成字符串存储
     *
     * @param obj 内容
     * @return {@link JsonAry}
     */
    public JsonAry add(final Object obj) {
        node.add(toStr(obj));
        return this;
    }

    /**
     * 添加一个对象
     *
     * @param obj 内容
     * @return {@link JsonAry}
     */
    public JsonAry addObj(final Object obj) {
        node.add(toNode(toStr(obj)));
        return this;
    }

    /**
     * 添加一个{@link JsonAry}
     *
     * @param con 构建器
     * @return {@link JsonAry}
     */
    public JsonAry addAry(final Consumer<JsonAry> con) {
        final JsonAry ary = JsonAry.of(node.arrayNode());
        con.accept(ary);
        addObj(ary.toJson());
        return this;
    }

    /**
     * 添加一个集合
     *
     * @param coll 集合
     * @return {@link JsonAry}
     */
    public JsonAry addColl(final Collection<?> coll) {
        ofNullable(coll).ifPresent(e -> e.forEach(this::add));
        return this;
    }

    /**
     * 构建成json数组字符串
     *
     * @return json数组
     */
    public String toJson() {
        return node.toString();
    }

    /**
     * 构建成json数组字符串，格式化
     *
     * @return json数组
     */
    public String toPrettyJson() {
        return node.toPrettyString();
    }

    /**
     * 转成List
     *
     * @param <T> 元素类型
     * @return {@link  List}
     */
    public <T> List<T> toList() {
        return Json.toList(toJson());
    }
}
