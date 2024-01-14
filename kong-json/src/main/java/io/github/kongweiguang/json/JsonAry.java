package io.github.kongweiguang.json;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Collection;

import static java.util.Optional.ofNullable;

/**
 * json数组
 * @author kongweiguang
 */
public class JsonAry {
    private ArrayNode node = Json.mapper().createArrayNode();

    public JsonAry() {
    }

    public JsonAry(ArrayNode node) {
        this.node = node;
    }

    public static JsonAry of() {
        return new JsonAry();
    }

    public static JsonAry of(ArrayNode node) {
        return new JsonAry(node);
    }

    public JsonAry add(Object obj) {
        node.add(Json.toStr(obj));
        return this;
    }

    public JsonAry add(Collection<?> coll) {
        ofNullable(coll).ifPresent(e -> e.forEach(this::add));
        return this;
    }

    public String build() {
        return node.toString();
    }
}
