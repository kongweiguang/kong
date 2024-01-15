package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

public class BodyTest {

    final User kkk = new User().setAge(12).setHobby(new String[]{"a", "b", "c"}).setName("kkk");

    @Test
    void test1() throws Exception {
        final Res res = Req.post("http://localhost:8080/post_body")
                //        .body("{}")
                //自动会将对象转成json字符串，使用jackson
                .json(kkk)
//                .body("text".getBytes(StandardCharsets.UTF_8),ContentType.text_plain)
                .body("text", ContentType.text_plain)
                .ok();
        System.out.println("res.str() = " + res.str());
    }

}
