package json;

import io.github.kongweiguang.json.Json;
import io.github.kongweiguang.json.JsonAry;
import io.github.kongweiguang.json.JsonObj;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class JsonBuilderTest {

    User u = new User().setAge(1).setName("kong").setHobby(new String[]{"j", "n"});

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


    @Test
    void test1() throws Exception {
        final JsonObj jsonObj = Json.obj().put("1", "true");
        System.out.println(jsonObj.toMap());
        System.out.println(jsonObj.toJson());
    }

    @Test
    void test2() throws Exception {
        final String json = Json.ary().add(1).add(new BigDecimal(2)).addAry(e -> e.add(66).add(888)).addObj(u).toJson();
        System.out.println(json);
    }

    @Test
    void test3() throws Exception {
        System.out.println(Json.obj().put("1", null).toPrettyJson());
    }
}
