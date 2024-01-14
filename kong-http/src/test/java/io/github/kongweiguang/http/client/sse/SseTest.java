package io.github.kongweiguang.http.client.sse;

import io.github.kongweiguang.core.Threads;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class SseTest {


    @Test
    void test() throws InterruptedException {

        Req.sse("localhost:8080/sse")
                .sseListener(new SSEListener() {
                    @Override
                    public void event(Req req, SseEvent msg) {
                        System.out.println("sse -> " + msg.id());
                        System.out.println("sse -> " + msg.type());
                        System.out.println("sse -> " + msg.data());
                        if (Objects.equals(msg.data(), "done")) {
                            close();
                        }
                    }

                    @Override
                    public void open(final Req req, final Res res) {
                        super.open(req, res);
                    }

                    @Override
                    public void fail(final Req req, final Res res, final Throwable t) {
                        super.fail(req, res, t);
                    }

                    @Override
                    public void closed(final Req req) {
                        super.closed(req);
                    }
                })
                .ok();

        Threads.sync(this);
    }

}
