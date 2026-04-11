package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ResTest {

    @Test
    void testRes() {
        Res userRes = Req.get(TestHttpServer.url("/user")).ok();
        User user = userRes.obj(User.class);
        Assertions.assertEquals("tom", user.getName());

        Res usersRes = Req.get(TestHttpServer.url("/users")).ok();
        List<User> users = usersRes.list(User.class);
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals("tom", users.get(0).getName());

        Res mapRes = Req.get(TestHttpServer.url("/map")).ok();
        Map<String, String> map = mapRes.map(String.class, String.class);
        Assertions.assertEquals("v", map.get("k"));

        Res intRes = Req.get(TestHttpServer.url("/int")).ok();
        Assertions.assertEquals(123, intRes.i32());

        Res boolRes = Req.get(TestHttpServer.url("/bool")).ok();
        Assertions.assertTrue(boolRes.bool());

        Res stringRes = Req.get(TestHttpServer.url("/get_string")).ok();
        String str = stringRes.str();
        Assertions.assertEquals("hello", str);

        Res bytesRes = Req.get(TestHttpServer.url("/get_string")).ok();
        byte[] bytes = bytesRes.bytes();
        Assertions.assertEquals("hello", new String(bytes));

        Res streamRes = Req.get(TestHttpServer.url("/get_string")).ok();
        InputStream stream = streamRes.stream();
        Assertions.assertNotNull(stream);

        Res headerRes = Req.get(TestHttpServer.url("/get_string")).ok();
        Map<String, List<String>> headers = headerRes.headers();
        Assertions.assertTrue(headers.containsKey("Content-type") || headers.containsKey("Content-Type"));

        int status = headerRes.code();
        Assertions.assertEquals(200, status);

        Response response = headerRes.raw();
        Assertions.assertNotNull(response);
    }

}
