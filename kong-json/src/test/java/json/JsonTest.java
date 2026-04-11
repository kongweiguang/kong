package json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.kongweiguang.core.exception.KongException;
import io.github.kongweiguang.json.Json;
import io.github.kongweiguang.json.JsonAry;
import io.github.kongweiguang.json.JsonObj;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonTest {

    private static final JsonMapper DEFAULT_MAPPER = Json.mapper();

    private final User user = new User().setAge(1).setName("kong").setHobby(new String[]{"j", "n"});

    @AfterEach
    void restoreMapper() {
        Json.mapper(DEFAULT_MAPPER);
    }

    @Test
    void objectBuilderWritesNativeJsonTypes() {
        JsonObj jsonObj = Json.obj()
                .put("name", "kong")
                .put("age", 1)
                .put("active", true)
                .put("salary", new BigDecimal("88.50"))
                .put("nullable", null)
                .putObj("profile", user)
                .putAry("tags", ary -> ary.add("java").add(8))
                .putString("literalNumber", 1);

        JsonNode node = jsonObj.toNode();

        assertEquals("kong", node.get("name").asText());
        assertTrue(node.get("age").isInt());
        assertEquals(1, node.get("age").asInt());
        assertTrue(node.get("active").isBoolean());
        assertTrue(node.get("active").asBoolean());
        assertTrue(node.get("salary").isBigDecimal());
        assertTrue(node.get("nullable").isNull());
        assertTrue(node.get("profile").isObject());
        assertEquals("kong", node.get("profile").get("name").asText());
        assertTrue(node.get("tags").isArray());
        assertEquals(8, node.get("tags").get(1).asInt());
        assertEquals("1", node.get("literalNumber").asText());
    }

    @Test
    void arrayBuilderWritesNativeJsonTypes() {
        JsonAry jsonAry = Json.ary()
                .add(1)
                .add(true)
                .add(null)
                .addObj(user)
                .addAry(ary -> ary.add("nested").add(false))
                .addString(66);

        JsonNode node = jsonAry.toNode();

        assertTrue(node.isArray());
        assertTrue(node.get(0).isInt());
        assertTrue(node.get(1).isBoolean());
        assertTrue(node.get(2).isNull());
        assertTrue(node.get(3).isObject());
        assertEquals("kong", node.get(3).get("name").asText());
        assertTrue(node.get(4).isArray());
        assertFalse(node.get(4).get(1).asBoolean());
        assertEquals("66", node.get(5).asText());
    }

    @Test
    void putMapAndAddCollPreserveNestedTypes() {
        JsonObj jsonObj = Json.obj()
                .putMap(Map.of(
                        "count", 2,
                        "enabled", true,
                        "items", List.of("a", "b"),
                        "user", user
                ));

        JsonAry jsonAry = Json.ary()
                .addColl(List.of(1, true, Map.of("name", "kong")));

        assertEquals(2, jsonObj.toNode().get("count").asInt());
        assertTrue(jsonObj.toNode().get("enabled").asBoolean());
        assertEquals("b", jsonObj.toNode().get("items").get(1).asText());
        assertEquals("kong", jsonObj.toNode().get("user").get("name").asText());
        assertEquals(1, jsonAry.toNode().get(0).asInt());
        assertTrue(jsonAry.toNode().get(1).asBoolean());
        assertEquals("kong", jsonAry.toNode().get(2).get("name").asText());
    }

    @Test
    void conversionApisSupportStringNodeAndPojoInputs() {
        String json = "{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}";

        User fromString = Json.toObj(json, User.class);
        JsonNode node = Json.toNode(fromString);
        Person fromNode = Json.toObj(node, Person.class);

        assertEquals("kong", fromString.getName());
        assertEquals(1, fromString.getAge());
        assertArrayEquals(new String[]{"j", "n"}, fromString.getHobby());
        assertEquals("kong", fromNode.getName());
        assertArrayEquals(new String[]{"j", "n"}, fromNode.getHobby());
        assertEquals(fromString.getName(), Json.toObj("kong", String.class));
    }

    @Test
    void listAndMapConversionsPreserveGenericTypes() {
        String usersJson = "[{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}]";
        String mapJson = "{\"name\":[\"j\",\"n\"],\"age\":[\"1\"],\"hobby\":[\"j\",\"n\"]}";

        List<User> users = Json.toList(usersJson, User.class);
        List<Person> persons = Json.toList(users, Person.class);
        Map<String, List<String>> map = Json.toMap(mapJson, new TypeReference<Map<String, List<String>>>() {
        });

        assertEquals(1, users.size());
        assertEquals("kong", users.getFirst().getName());
        assertEquals("kong", persons.getFirst().getName());
        assertEquals(List.of("j", "n"), map.get("name"));
        assertEquals(List.of("1"), map.get("age"));
    }

    @Test
    void readAndReadJsonlUseConsistentParsing(@TempDir Path tempDir) throws IOException {
        Path jsonFile = tempDir.resolve("sample.json");
        Files.writeString(jsonFile, "{\"name\":\"kong\",\"age\":1}", StandardCharsets.UTF_8);

        Path jsonlFile = tempDir.resolve("sample.jsonl");
        Files.writeString(jsonlFile, "{\"id\":1}\n{\"id\":2}", StandardCharsets.UTF_8);

        JsonNode jsonNode = Json.read(jsonFile);
        List<JsonNode> jsonl = Json.readJsonl(jsonlFile);

        assertEquals("kong", jsonNode.get("name").asText());
        assertEquals(2, jsonl.size());
        assertEquals(1, jsonl.getFirst().get("id").asInt());
        assertEquals(2, jsonl.get(1).get("id").asInt());
    }

    @Test
    void readApisWrapIoFailures(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("missing.json");

        assertThrows(KongException.class, () -> Json.read(missing));
        assertThrows(KongException.class, () -> Json.readJsonl(missing));
    }

    @Test
    void customMapperRequiresNonNullAndCanBeReplaced() {
        assertThrows(IllegalArgumentException.class, () -> Json.mapper(null));

        JsonMapper customMapper = JsonMapper.builder().build();
        Json.mapper(customMapper);

        assertEquals(customMapper, Json.mapper());
        assertNotNull(Json.obj().toNode());
        assertNotNull(Json.ary().toNode());
    }

    @Test
    void conversionApisHandleNullInputsConsistently() {
        assertNull(Json.toStr(null));
        assertNull(Json.toObj(null, User.class));
        assertNull(Json.toObj(null, new TypeReference<List<User>>() {
        }));
        assertNull(Json.toObj(null, Json.javaType(List.class, User.class)));
        assertNull(Json.toNode(null));
        assertNull(Json.toList(null, User.class));
        assertNull(Json.toMap(null, String.class, Object.class));
    }

    @Test
    void invalidJsonIsWrappedInKongException() {
        KongException exception = assertThrows(KongException.class, () -> Json.toObj("{bad json}", User.class));
        assertInstanceOf(Exception.class, exception.getCause());
    }
}
