package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FormTest {

    @Test
    public void testForm() throws IOException {
        Res ok = Req.formUrlencoded(TestHttpServer.url("/post_form"))
                .form("a", "1")
                .form(new HashMap<>() {{
                    put("b", "2");
                }})
                .ok();

        String body = ok.str();
        Assertions.assertTrue(body.contains("a=1"));
        Assertions.assertTrue(body.contains("b=2"));
    }

    @Test
    public void test2() throws Exception {
        Res ok = Req.multipart(TestHttpServer.url("/post_mul_form"))
                .file("test", "test.txt", "file-content".getBytes(StandardCharsets.UTF_8))
                .form("a", "1")
                .form(new HashMap<>() {{
                    put("b", "2");
                }})
                .ok();

        String body = ok.str();
        Assertions.assertTrue(body.contains("name=\"test\""));
        Assertions.assertTrue(body.contains("file-content"));
        Assertions.assertTrue(body.contains("name=\"a\""));
        Assertions.assertTrue(body.contains("name=\"b\""));
    }
}
