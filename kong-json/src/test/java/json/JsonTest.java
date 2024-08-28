package json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.kongweiguang.json.Json;
import io.github.kongweiguang.json.JsonAry;
import io.github.kongweiguang.json.JsonObj;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JsonTest {

    User u = new User().setAge(1).setName("kong").setHobby(new String[]{"j", "n"});

    /**
     * {
     * "a" : "b",
     * "c" : [ "d1", "d2" ],
     * "e" : "f",
     * "g" : [ "1", "2", "3", "4", "4" ],
     * "u1" : "{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}",
     * "u2" : {
     * "name" : "kong",
     * "age" : 1,
     * "hobby" : [ "j", "n" ]
     * },
     * "i" : "1"
     * }
     * 4
     *
     * @throws Exception
     */
    @Test
    void testJsonObj() throws Exception {

        final String str = JsonObj.of()
                .put("a", "b")
                .putAry("c", o -> o.add("d1").add("d2"))
                .put("e", "f")
                .putAry("g", c -> c.addColl(Arrays.asList(1, 2, 3, 4, 4)))
                .put("u1", u)
                .putObj("u2", u)
                .put("i", 1)
                .toPrettyJson();

        System.out.println(str);

        System.out.println(Json.toNode(str).get("g").get(3).asInt());
    }

    /**
     * ["1","2","3",{"name":"kong","age":1,"hobby":["j","n"]},{"a":"a","b":"b"},["6","7"],"0","0"]
     * [1, 2, 3, {name=kong, age=1, hobby=[j, n]}, {a=a, b=b}, [6, 7], 0, 0]
     *
     * @throws Exception
     */
    @Test
    void testJsonAry() throws Exception {

        final String ary = JsonAry.of()
                .add(1)
                .add(2)
                .add(3)
                .addObj(u)
                .addObj(c -> c.put("a", "a").put("b", "b"))
                .addAry(c -> c.add(6).add(7))
                .addColl(Arrays.asList(0, 0))
                .toJson();

        System.out.println(ary);

        final List<Object> list = Json.toList(ary, Object.class);

        System.out.println(list);
    }

    /**
     * {1=true}
     * {"1":"true"}
     *
     * @throws Exception
     */
    @Test
    void test1() throws Exception {
        final JsonObj jsonObj = Json.obj().put("1", "true");
        System.out.println(jsonObj.toMap(Object.class, Object.class));
        System.out.println(jsonObj.toJson());
    }

    /**
     * ["1","2",["66","888"],{"name":"kong","age":1,"hobby":["j","n"]}]
     *
     * @throws Exception
     */
    @Test
    void test2() throws Exception {
        final String json = Json.ary().add(1).add(new BigDecimal(2)).addAry(e -> e.add(66).add(888)).addObj(u).toJson();
        System.out.println(json);
    }

    /**
     * {
     * "1" : null
     * }
     *
     * @throws Exception
     */
    @Test
    void test3() throws Exception {
        System.out.println(Json.obj().put("1", null).toPrettyJson());
    }

    @Test
    public void test4() throws Exception {
        String json = "[{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}]";
        List<User> users = Json.mapper().readValue(json, new TypeReference<List<User>>() {
        });
        System.out.println(users);
    }

    @Test
    public void test5() throws Exception {
        String json = "{\"name\":[\"j\",\"n\"],\"age\":[\"j\",\"n\"],\"hobby\":[\"j\",\"n\"]}";
        Map<String, List<String>> map = Json.toMap(json, new TypeReference<Map<String, List<String>>>() {
        });
        System.out.println("map = " + map);
    }

    @Test
    public void test6() throws Exception {
        String json = "{\"name\":[\"j\",\"n\"],\"age\":[\"j\",\"n\"],\"hobby\":[\"j\",\"n\"]}";
        JsonNode node = Json.toNode(json);
        Map<String, List<String>> map = Json.toMap(node, new TypeReference<Map<String, List<String>>>() {
        });
        System.out.println("map = " + map);
    }

    @Test
    public void test7() throws Exception {
        String json = "{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}";
        User node = Json.toObj(json, User.class);
        System.out.println("node = " + node);
        Person obj = Json.toObj(node, Person.class);
        System.out.println("obj = " + obj);
    }

    @Test
    public void test8() throws Exception {
        String json = "name";
        String obj = Json.toObj(json, String.class);
        System.out.println("obj = " + obj);
    }

    @Test
    public void test9() throws Exception {
        String json = "{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}";
        User node = Json.toObj(json, User.class);
        JsonNode node1 = Json.toNode(node);
        System.out.println("node1 = " + node1);

        JsonNode node2 = Json.toNode(json);
        System.out.println("node2 = " + node2);
    }

    @Test
    public void test10() throws Exception {
        String json = "[{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}]";
        List<Object> list = Json.toList(json, Object.class);
        System.out.println("list = " + list);
        List<User> list1 = Json.toList(json, User.class);

        System.out.println("list1 = " + list1);

        List<Person> list2 = Json.toList(json, new TypeReference<List<Person>>() {
        });

        System.out.println("list2 = " + list2);

        List<Person> list3 = Json.toList(list1, Person.class);
        System.out.println("list3 = " + list3);


    }

    @Test
    public void test11() throws Exception {
        String json = "{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}";
        Map<Object, Object> map = Json.toMap(json, Object.class, Object.class);

    }
}
