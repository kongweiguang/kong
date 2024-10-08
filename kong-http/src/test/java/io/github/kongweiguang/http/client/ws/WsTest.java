package io.github.kongweiguang.http.client.ws;

import io.github.kongweiguang.core.util.Threads;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.ReqBuilder;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

public class WsTest {

    @Test
    void test() {
        final WSListener listener = new WSListener() {
            @Override
            public void open(final ReqBuilder req, final Res res) {
                send("hello");
            }

            @Override
            public void msg(final ReqBuilder req, final String text) {
                System.out.println("text -> " + text);
            }

            @Override
            public void msg(final ReqBuilder req, final byte[] bytes) {
                super.msg(req, bytes);
            }

            @Override
            public void fail(final ReqBuilder req, final Res res, final Throwable t) {
                super.fail(req, res, t);
            }

            @Override
            public void closing(final ReqBuilder req, final int code, final String reason) {
                super.closing(req, code, reason);
            }

            @Override
            public void closed(final ReqBuilder req, final int code, final String reason) {
                super.closed(req, code, reason);
            }
        };

        final Res res = Req.ws("ws://127.0.0.1:8080/ws/k")
                .query("k", "v")
                .wsListener(listener)
                .ok();
        Threads.sleep(1000);

        for (int i = 0; i < 3; i++) {
            listener.send("123");
        }

        Threads.sync(new Object());
    }

}
