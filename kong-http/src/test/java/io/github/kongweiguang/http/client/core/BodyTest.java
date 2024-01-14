package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

public class BodyTest {

    @Test
    void test1() throws Exception {
        final User kkk = new User().setAge(12).setHobby(new String[]{"a", "b", "c"}).setName("kkk");
        final Res res = Req.post("http://localhost:8080/post_body")
                //        .body(JSON.toJSONString(kkk))
                //        .body("{}")
                //自动会将对象转成json对象，使用fastjson2
                .json(kkk)
                //        .body("text", ContentType.text_plain)
                .ok();
        System.out.println("res.str() = " + res.str());
    }

}
