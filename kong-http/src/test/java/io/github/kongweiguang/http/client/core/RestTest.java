package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

public class RestTest {

    @Test
    void testGet() {
        final Res res = Req.get(TestHttpServer.url("/echo"))
                .query("a", "1")
                .query("b", "2")
                .query("c", "3")
                .query("d", Arrays.asList("0", "9", "8"))
                .ok();

        String query = res.str();
        Assertions.assertTrue(query.contains("a=1"));
        Assertions.assertTrue(query.contains("b=2"));
        Assertions.assertTrue(query.contains("c=3"));
        Assertions.assertTrue(query.contains("d=0"));
        Assertions.assertTrue(query.contains("d=9"));
        Assertions.assertTrue(query.contains("d=8"));
    }

    @Test
    void testPost() {
        final Res res = Req.post(TestHttpServer.url("/post_json"))
                .query("b", "b")
                .json(new HashMap<String, Object>() {{
                    put("a", "1");
                    put("b", "2");
                    put("c", "3");
                }})
                .ok();

        String body = res.str();
        Assertions.assertTrue(body.startsWith("b=b|"));
        Assertions.assertTrue(body.contains("\"a\":\"1\""));
        Assertions.assertTrue(body.contains("\"b\":\"2\""));
        Assertions.assertTrue(body.contains("\"c\":\"3\""));
    }

}
