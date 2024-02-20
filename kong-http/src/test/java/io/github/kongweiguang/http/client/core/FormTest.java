package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class FormTest {

    @Test
    void testForm() throws IOException {
        //application/x-www-form-urlencoded
        final Res ok = Req.formUrlencoded("http://localhost:8080/post_form")
                .form("a", "1")
                .form(new HashMap<String, Object>() {{
                    put("b", "2");
                }})
                .ok();
        System.out.println("ok.str() = " + ok.str());
    }

    @Test
    void test2() throws Exception {
        //multipart/form-data
        final Res ok = Req.multipart("http://localhost:8080/post_mul_form")
                .file("k", "k.txt", Files.readAllBytes(Paths.get("/Users/kongweiguang/Desktop/Snipaste_2023-12-25_14-25-51.png")))
                .form("a", "1")
                .form(new HashMap<String, Object>() {{
                    put("b", "2");
                }})
                .ok();
        System.out.println("ok.str() = " + ok.str());
    }
}
