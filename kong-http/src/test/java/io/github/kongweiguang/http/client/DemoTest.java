package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.core.Timeout;
import io.github.kongweiguang.http.client.core.UA;

import java.time.Duration;

public class DemoTest {

    public static void main(String[] args) {
        final Res ok = Req.post("https://open.feishu.cn/open-apis/bot/v2/hook/409771ec-d1cf-4dba-8c98-2b64eee8328f")
                .config(e -> e.timeout(new Timeout(Duration.ofMinutes(10), Duration.ofMinutes(10), Duration.ofMinutes(10))))
                .header("k", "v")
                .header("q", "wer")
                .query("q1", "v1")
                .query("q2", "我是vvv")
                .fragment("123")
                .ua(UA.Win.chrome.v())
                .json("{\"content\":{\"text\":\"123\"},\"msg_type\":\"text\"}")
                .ok();
        System.out.println(ok);
        System.out.println(123);
    }
}
