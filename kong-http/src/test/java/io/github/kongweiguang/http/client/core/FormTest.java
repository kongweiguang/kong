package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class FormTest {

    @Test
    public void testForm() throws IOException {
        //application/x-www-form-urlencoded
        Res ok = Req.formUrlencoded("http://localhost:8080/post_form")
                .form("a", "1")
                .form(new HashMap<>() {{
                    put("b", "2");
                }})
                .ok();
        Assertions.assertEquals("ok", ok.str());
        //{a=[1], b=[2]}
    }

    @Test
    public void test2() throws Exception {
        //multipart/form-data
        Res ok = Req.multipart("http://localhost:8080/post_mul_form")
                .file("test", "test.txt", Files.readAllBytes(Paths.get("C:", "test", "test.txt")))
                .form("a", "1")
                .form(new HashMap<>() {{
                    put("b", "2");
                }})
                .ok();
        Assertions.assertEquals("ok", ok.str());
        //params = {a=[1], b=[2]}
        //files = {test=[io.github.kongweiguang.http.server.core.UploadFile@6231d793]}
    }
}
