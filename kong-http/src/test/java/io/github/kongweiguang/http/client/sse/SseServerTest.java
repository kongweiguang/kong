package io.github.kongweiguang.http.client.sse;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.ReqBuilder;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.core.Method;
import io.github.kongweiguang.http.server.JavaServer;
import io.github.kongweiguang.http.server.core.HttpReq;
import io.github.kongweiguang.http.server.core.HttpRes;
import io.github.kongweiguang.http.server.sse.SSEHandler;
import io.github.kongweiguang.json.Json;
import org.junit.jupiter.api.Test;

public class SseServerTest {
    @Test
    public void test() throws Exception {
        JavaServer.of()
                .rest("/sse", new SSEHandler() {
                    @Override
                    public void handler(HttpReq httpReq, HttpRes httpRes) {
                        String json1 = Json.obj()
                                .put("model", "gemma3:1b")
                                .putObj("stream", true)
                                .putAry("messages", ary -> {
                                    ary.addObj(o -> o.put("role", "user").put("content", "介绍一下武汉吧!"));
                                }).toJson();
                        System.out.println("json1 = " + json1);
                        Req.sse("http://127.0.0.1:11434/v1/chat/completions")
                                .method(Method.POST)
                                .sseListener(new SSEListener() {
                                    @Override
                                    public void event(ReqBuilder reqBuilder, SseEvent sseEvent) {
                                        System.out.println("sseEvent = " + sseEvent);
                                        send(httpRes, SseEvent.of().data(sseEvent.data()));
                                    }

                                    @Override
                                    public void open(ReqBuilder req, Res res) {
                                        System.out.println("open ");
                                    }

                                    @Override
                                    public void fail(ReqBuilder req, Res res, Throwable t) {
                                        System.out.println("res = " + res);
                                        System.out.println("t = " + t);
                                    }

                                    @Override
                                    public void closed(ReqBuilder req) {
                                        System.out.println("closed");
                                    }
                                })
                                .json(json1)
                                .okAsync();
                    }
                })
                .ok(8888);
    }
}
