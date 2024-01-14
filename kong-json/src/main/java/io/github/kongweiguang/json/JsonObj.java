package io.github.kongweiguang.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;
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

    public static JsonObj of() {
        return new JsonObj();
    }


    public static JsonObj of(final ObjectNode node) {
        return new JsonObj(node);
    }

    public JsonObj put(String k, String v) {
        node.put(k, Json.toStr(v));
        return this;
    }

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
        final ArrayNode jsonNodes = node.putArray(k);

        for (Object o : coll) {
            jsonNodes.add(Json.toNode(Json.toStr(o)));
        }

        return this;
    }

    public String build() {
        return node.toPrettyString();
    }
}
