package io.github.kongweiguang.http.client.ws;

import io.github.kongweiguang.core.Threads;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

public class WsTest {

    @Test
    void test() {
        final WSListener listener = new WSListener() {
            @Override
            public void open(final Req req, final Res res) {
                send("hello");
            }

            @Override
            public void msg(final Req req, final String text) {
                System.out.println(text);
                send("hello");
            }

            @Override
            public void msg(final Req req, final byte[] bytes) {
                super.msg(req, bytes);
            }

            @Override
            public void fail(final Req req, final Res res, final Throwable t) {
                super.fail(req, res, t);
            }

            @Override
            public void closing(final Req req, final int code, final String reason) {
                super.closing(req, code, reason);
            }

            @Override
            public void closed(final Req req, final int code, final String reason) {
                super.closed(req, code, reason);
            }
        };

        final Res res = Req.ws("ws://127.0.0.1:8080/ws/k")
                .query("k", "v")
                .wsListener(listener)
                .ok();

        for (int i = 0; i < 3; i++) {
            listener.send("123");
        }

        //res == null
//    Util.sleep(Integer.MAX_VALUE);
        Threads.sync(new Object());
    }

}
