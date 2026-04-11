package io.github.kongweiguang.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

final class JsonValueHelper {

    private JsonValueHelper() {
    }

    static JsonNode valueToNode(Object value) {
        if (value == null) {
            return NullNode.getInstance();
        }

        if (value instanceof JsonNode jsonNode) {
            return jsonNode;
        }

        if (value instanceof JsonObj jsonObj) {
            return jsonObj.toNode();
        }

        if (value instanceof JsonAry jsonAry) {
            return jsonAry.toNode();
        }

        if (value instanceof String text) {
            return TextNode.valueOf(text);
        }

        // Delegate all structured values to Jackson so maps, collections, arrays and beans
        // follow one consistent conversion path across both object and array builders.
        return Json.mapper().valueToTree(value);
    }

    static JsonNode stringToNode(Object value) {
        if (value == null) {
            return NullNode.getInstance();
        }

        return TextNode.valueOf(String.valueOf(value));
    }

    static void putValue(ObjectNode node, String fieldName, Object value) {
        node.set(fieldName, valueToNode(value));
    }

    static void putString(ObjectNode node, String fieldName, Object value) {
        node.set(fieldName, stringToNode(value));
    }

    static void addValue(ArrayNode node, Object value) {
        node.add(valueToNode(value));
    }

    static void addString(ArrayNode node, Object value) {
        node.add(stringToNode(value));
    }
}
