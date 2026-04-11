package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

public class UrlQueryTest {

    @Test
    void test1() throws Exception {
        Res res = Req.get(TestHttpServer.url("/echo") + "?q=1")
                .query("k1", "v1")
                .query("k2", Arrays.asList("1", "2"))
                .query(new HashMap<String, Object>() {{
                    put("k3", "v3");
                    put("k4", "v4");
                }})
                .ok();

        String query = res.str();
        Assertions.assertTrue(query.contains("q=1"));
        Assertions.assertTrue(query.contains("k1=v1"));
        Assertions.assertTrue(query.contains("k2=1"));
        Assertions.assertTrue(query.contains("k2=2"));
        Assertions.assertTrue(query.contains("k3=v3"));
        Assertions.assertTrue(query.contains("k4=v4"));
    }

}
