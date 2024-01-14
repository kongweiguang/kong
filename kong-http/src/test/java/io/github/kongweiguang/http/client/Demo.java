package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.core.ReqLog;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;

public class Demo {
    @Test
    public void test() throws Exception {
        Req.get("https://www.baidu.com")
                .cookie("1", "3")
                .cookie("3", "34")
                .header("bd", "weqwe")

                .headers(new HashMap<String, String>() {{
                    put("1,2", "jlsdjf");
                    put("1,4", "jlsdjf");
                    put("B", "BBB");
                }})
                .header("B", "LLLL")
                .json("[1,3,4]")
                .log(ReqLog.console, HttpLoggingInterceptor.Level.BODY)
                .timeout(Duration.ofMillis(1000))
                .ok()
                .then(r -> {
                    System.out.println(r.code());
                    System.out.println("ok -> " + r.isOk());
                })
                .then(r -> {
                    System.out.println("redirect -> " +r.isRedirect());
                });
    }
}
