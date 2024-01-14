package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

public class RestTest {

    @Test
    void testGet() {
        final Res res = Req.get("http://localhost:8080/get_string")
                .query("a", "1")
                .query("b", "2")
                .query("c", "3")
                .query("d", Arrays.asList("0", "9", "8"))
                .ok();
        System.out.println("res = " + res.str());
    }

    @Test
    void testPost() {
        final Res res = Req.post("http://localhost:8080/post_json")
                .query("b", "b")
                .json(new HashMap<String, Object>() {{
                    put("a", "1");
                    put("b", "2");
                    put("c", "3");
                }})
                .ok();
        System.out.println("res = " + res.str());
    }


}
