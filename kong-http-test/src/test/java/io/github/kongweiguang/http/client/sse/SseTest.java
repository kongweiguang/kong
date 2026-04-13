package io.github.kongweiguang.http.client.sse;

import io.github.kongweiguang.core.threads.Threads;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.spec.ReqSpec;
import io.github.kongweiguang.http.client.spec.SseReqSpec;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class SseTest {


    @Test
    void test() throws InterruptedException {
        SseListener listener = new SseListener() {
            @Override
            public void event(SseReqSpec req, SseEvent msg) {
                System.out.println("sse -> " + msg.id());
                System.out.println("sse -> " + msg.type());
                System.out.println("sse -> " + msg.data());
                if (Objects.equals(msg.data(), "done")) {
                    closeCon();
                }
            }

            @Override
            public void open(SseReqSpec req, Res res) {
                System.out.println(req);
                System.out.println(res);
            }

            @Override
            public void fail(SseReqSpec req, Res res, Throwable t) {
                System.out.println("fail" + t);
            }

            @Override
            public void closed(SseReqSpec req) {
                System.out.println("close");
            }
        };

        EventSource es = Req.sse("http://localhost:8080/sse")
                .sseListener(listener)
                .ok();

        Request request = es.request();

        Threads.sync(this);
    }

}
