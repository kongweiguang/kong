package io.github.kongweiguang.http.client.sse;

import io.github.kongweiguang.core.threads.Threads;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.builder.SSEReqBuilder;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class SseTest {


    @Test
    void test() throws InterruptedException {
        SSEListener listener = new SSEListener() {
            @Override
            public void event(SSEReqBuilder req, SseEvent msg) {
                System.out.println("sse -> " + msg.id());
                System.out.println("sse -> " + msg.type());
                System.out.println("sse -> " + msg.data());
                if (Objects.equals(msg.data(), "done")) {
                    closeCon();
                }
            }

            @Override
            public void open(SSEReqBuilder req, Res res) {
                System.out.println(req);
                System.out.println(res);
            }

            @Override
            public void fail(SSEReqBuilder req, Res res, Throwable t) {
                System.out.println("fail" + t);
            }

            @Override
            public void closed(SSEReqBuilder req) {
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
