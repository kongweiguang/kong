package io.github.kongweiguang.json;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Collection;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * json数组
 *
 * @author kongweiguang
 */
public class JsonAry {
    private ArrayNode node = Json.mapper().createArrayNode();

    public JsonAry() {
    }

    public JsonAry(ArrayNode node) {
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
    public static JsonAry of(ArrayNode node) {
        return new JsonAry(node);
    }

    /**
     * 添加一个字符串
     *
     * @param str 内容
     * @return {@link JsonAry}
     */
    public JsonAry add(String str) {
        node.add(str);
        return this;
    }

    /**
     * 添加一个对象
     *
     * @param obj 内容
     * @return {@link JsonAry}
     */
    public JsonAry add(Object obj) {
        if (obj instanceof String) {
            return add((String) obj);
        }

        if (obj instanceof Collection) {
            return add(((Collection<?>) obj));
        }

        node.add(Json.toNode(Json.toStr(obj)));
        return this;
    }

    /**
     * 添加一个集合
     *
     * @param coll 集合
     * @return {@link JsonAry}
     */
    public JsonAry add(Collection<?> coll) {
        ofNullable(coll).ifPresent(e -> e.forEach(this::add));
        return this;
    }

    /**
     * 构建成json数组字符串
     *
     * @return json数组
     */
    public String build() {
        return node.toString();
    }

    /**
     * 转成List
     *
     * @param <T> 元素类型
     * @return {@link  List}
     */
    public <T> List<T> toList() {
        return Json.toList(build());
    }
}
