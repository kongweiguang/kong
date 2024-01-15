package io.github.kongweiguang.http.client.sse;

import io.github.kongweiguang.core.Threads;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.ReqBuilder;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Objects;

public class SseTest {


    @Test
    void test() throws InterruptedException {

        Req.sse("http://localhost:8080/sse")
                .sseListener(new SSEListener() {
                    @Override
                    public void event(ReqBuilder req, SseEvent msg) {
                        System.out.println("sse -> " + msg.id());
                        System.out.println("sse -> " + msg.type());
                        System.out.println("sse -> " + msg.data());
                        if (Objects.equals(msg.data(), "done")) {
                            close();
                        }
                    }

                    @Override
                    public void open(ReqBuilder req, Res res) {
                        System.out.println(req);
                        System.out.println(res);
                    }

                    @Override
                    public void fail(ReqBuilder req, Res res, Throwable t) {
                        System.out.println("fail"+t);
                    }

                    @Override
                    public void closed(ReqBuilder req) {
                        System.out.println("close");
                    }
                })
                .ok();

        Threads.sleep(Duration.ofSeconds(10).toMillis());
//        Threads.sync(this);
    }

}
