package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.UserAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HeaderTest {

    @Test
    void test1() throws Exception {
        final Res res = Req.get("http://localhost:8080/header")
                //contentype
                .contentType(ContentType.JSON.value())
                //charset
                .charset(StandardCharsets.UTF_8)
                //user-agent
                .userAgent(UserAgent.MacOS.CHROME.value())
                //authorization
                .auth("auth qwe")
                //authorization bearer
                .bearer("qqq")
                //header
                .header("name", "value")
                //headers
                .headers(new HashMap<>() {{
                    put("name1", "value1");
                    put("name2", "value2");
                }})
                //cookie
                .cookie("k", "v")
                //cookies
                .cookies(new HashMap<>() {{
                    put("k1", "v1");
                    put("k2", "v2");
                }})
                .ok();

        Assertions.assertEquals("ok", res.str());
        //headers = {Cookie=[k1=v1; k2=v2; k=v;], Accept-encoding=[gzip], Authorization=[Bearer qqq], Content-type=[application/json;charset=UTF-8], Connection=[Keep-Alive], Host=[localhost:8080], User-agent=[Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36], Name1=[value1], Name=[value], Name2=[value2]}
    }
}
