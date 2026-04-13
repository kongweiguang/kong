package io.github.kongweiguang.http.client.ws;

import io.github.kongweiguang.core.threads.Threads;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.spec.WsReqSpec;
import okhttp3.WebSocket;
import org.junit.jupiter.api.Test;

public class WsTest {

    @Test
    public void test() {
        WsListener listener = new WsListener() {
            @Override
            public void open(WsReqSpec req, Res res) {
                this.ws.send("123");
            }

            @Override
            public void msg(WsReqSpec req, String text) {
                System.out.println(text);
            }

            @Override
            public void closed(WsReqSpec req, int code, String reason) {
                System.out.println(reason);
            }
        };

        WebSocket ws = Req.ws("ws://localhost:8889/ws")
                .query("k", "v")
                .header("h", "v")
                .wsListener(listener)
                .ok();
        Threads.sleep(1000);

        for (int i = 0; i < 100; i++) {
            Threads.sleep(1000);
            ws.send("123");
        }

        Threads.sync(this);
    }

}
