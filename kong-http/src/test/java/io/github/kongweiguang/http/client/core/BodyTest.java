package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.json.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BodyTest {

    User user = new User().setAge(12).setHobby(new String[]{"a", "b", "c"}).setName("kkk");

    @Test
    public void test1() throws Exception {
        Res res = Req.post(TestHttpServer.url("/post_body"))
                .json(user)
                .ok();

        User responseUser = Json.toObj(res.str(), User.class);
        Assertions.assertEquals(user.getName(), responseUser.getName());
        Assertions.assertEquals(user.getAge(), responseUser.getAge());
        Assertions.assertArrayEquals(user.getHobby(), responseUser.getHobby());
    }

    @Test
    public void test2() throws Exception {
        Res res = Req.post(TestHttpServer.url("/post_body"))
                .body("text", ContentType.TEXT_PLAIN.v())
                .ok();

        Assertions.assertEquals("text", res.str());
    }

}
