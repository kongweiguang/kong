package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

public class UrlQueryTest {

    @Test
    void test1() throws Exception {
        //http://localhost:8080/get/one/two?q=1&k1=v1&k2=1&k2=2&k3=v3&k4=v4
        final Res res = Req.get("http://localhost:8080/get/one/two?q=1")
                .query("k1", "v1")
                .query("k2", Arrays.asList("1", "2"))
                .query(new HashMap<String, Object>() {{
                    put("k3", "v3");
                    put("k4", "v4");
                }})
                .ok();
    }

}
