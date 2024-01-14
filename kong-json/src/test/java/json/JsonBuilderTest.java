package json;

import io.github.kongweiguang.json.Json;
import io.github.kongweiguang.json.JsonAry;
import io.github.kongweiguang.json.JsonObj;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class JsonBuilderTest {

    User u = new User().setAge(1).setName("kong").setHobby(new String[]{"j", "n"});

    @Test
    void testJsonObj() throws Exception {

        final String str = JsonObj.of()
                .put("a", "b")
                .putAry("qq", o -> o.add("d").add("12312"))
                .put("bbb", "jflsdjf")
                .putAry("dd", c -> c.add(Arrays.asList(1, 2, 3, 4, 4)))
                .put("u", u)
                .put("i", 1)
                .build();


        System.out.println(str);
        System.out.println(Json.toNode(str).get("dd").get(3).asInt());
    }


    @Test
    void testJsonAry() throws Exception {

        final String ary = JsonAry.of().add(1).add(2).add(22).add(u).build();
        System.out.println(ary);
        final List<Object> list = Json.toList(ary);
        System.out.println("list = " + list);
    }

}
