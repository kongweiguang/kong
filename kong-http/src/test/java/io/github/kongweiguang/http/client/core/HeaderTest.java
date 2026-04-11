package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.UserAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HeaderTest {

    @Test
    void test1() throws Exception {
        final Res res = Req.get(TestHttpServer.url("/header"))
                .contentType(ContentType.JSON.v())
                .charset(StandardCharsets.UTF_8)
                .userAgent(UserAgent.Mac.chrome.v())
                .auth("auth qwe")
                .bearer("qqq")
                .header("name", "value")
                .headers(new HashMap<>() {{
                    put("name1", "value1");
                    put("name2", "value2");
                }})
                .cookie("k", "v")
                .cookies(new HashMap<>() {{
                    put("k1", "v1");
                    put("k2", "v2");
                }})
                .ok();

        String text = res.str();
        Assertions.assertTrue(text.contains("Authorization=Bearer qqq"));
        Assertions.assertTrue(text.contains("User-Agent=Mozilla/5.0"));
        Assertions.assertTrue(text.contains("name=value"));
        Assertions.assertTrue(text.contains("name1=value1"));
        Assertions.assertTrue(text.contains("name2=value2"));
        Assertions.assertTrue(text.contains("Cookie="));
    }
}
