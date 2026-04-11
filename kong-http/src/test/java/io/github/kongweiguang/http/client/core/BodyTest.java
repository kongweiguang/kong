package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.consts.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BodyTest {

    User user = new User().setAge(12).setHobby(new String[]{"a", "b", "c"}).setName("kkk");
    String json = """
            {
                "age": 12,
                "name": "kkk",
                "hobby": ["a", "b", "c"]
            }
            """;

    @Test
    public void test1() throws Exception {
        Res res = Req.post("http://localhost:8080/post_body")
                //自动会将对象转成json字符串，使用jackson
                .json(user)
                .ok();

        Assertions.assertEquals(json, res.body());
    }

    @Test
    public void test2() throws Exception {
        Res res = Req.post("http://localhost:8080/post_body")
                //自动会将对象转成json字符串，使用jackson
                .body("text", ContentType.TEXT_PLAIN.v())
                .ok();
        System.out.println("res.str() = " + res.str());
    }

}
