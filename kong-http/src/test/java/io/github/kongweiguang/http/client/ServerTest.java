package io.github.kongweiguang.http.client;


import com.sun.net.httpserver.Headers;
import io.github.kongweiguang.http.client.sse.SseEvent;
import io.github.kongweiguang.http.server.JavaServer;
import io.github.kongweiguang.http.server.core.HttpReq;
import io.github.kongweiguang.http.server.core.HttpRes;
import io.github.kongweiguang.http.server.core.MultiValueMap;
import io.github.kongweiguang.http.server.core.UploadFile;
import io.github.kongweiguang.http.server.sse.SSEHandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ServerTest {


    public static void main(String[] args) {

        JavaServer.of()
                .executor(Executors.newFixedThreadPool(8))
                //设置静态web地址，默认寻找index.html
                .web("/Users/kongweiguang/Desktop/hegui/xm/gs")
                .get("/get", (req, res) -> {
                    System.out.println("req = " + req.params());
                    res.send("ok");
                })
                .get("/get_string", (req, res) -> {
                    System.out.println("req = " + req.query());
                    System.out.println("req = " + req.params());
                    res.send("ok");
                })
                .post("/post_json", (req, res) -> {
                    final MultiValueMap<String, String> params = req.params();
                    System.out.println("params = " + params);

                    System.out.println("req.str() = " + req.str());

                    res.send("\"{\"key\":\"i am post res\"}\"");
                })
                .get("/get/one/two", (req, res) -> {
                    System.out.println("req = " + req.path());
                    System.out.println("params" + req.params());
                    res.send("ok");
                })
                .get("/header", (req, res) -> {
                    final Headers headers = req.headers();
                    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                        System.out.println("entry.getKey() = " + entry.getKey());
                        System.out.println("entry = " + entry.getValue());
                    }
                    res.send("ok");
                })
                //接受post请求
                .post("/post_body", ((req, res) -> {
                    final String str = req.str();
                    System.out.println("str = " + str);

                    res.send("{\"key\":\"i am post res\"}");
                }))
                .post("/post_form", ((req, res) -> {
                    System.out.println(req.params());
                    res.send("ok");
                }))
                .get("/timeout", ((req, res) -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    res.send("ok");
                }))
                .get("/error", ((req, res) -> {
                    System.out.println("req.str() = " + req.str());
                    res.write(500, "error_123456".getBytes());
                }))
                //上传
                .post("/post_mul_form", (req, res) -> {

                    final MultiValueMap<String, String> params = req.params();
                    System.out.println("params = " + params);
                    final Map<String, List<UploadFile>> files = req.fileMap();
                    System.out.println("files = " + files);
                    res.send("ok");
                })
                //下载文件
                .get("/xz", (req, res) ->
                        res.file("k.txt", Files.readAllBytes(Paths.get("D:\\k\\k.txt"))))
                //sse响应
                .get("/sse", new SSEHandler() {
                    @Override
                    public void handler(final HttpReq req, final HttpRes res) {
                        for (int i = 0; i < 3; i++) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            send(res,
                                    SseEvent.of()
                                            .id(UUID.randomUUID().toString())
                                            .type("eventType")
                                            .data(new Date().toString())
                            );
                        }

                        //完成
                        send(res,
                                SseEvent.of()
                                        .id(UUID.randomUUID().toString())
                                        .type("eventType")
                                        .data("done")
                        );

                        //关闭
                        close(res);
                    }
                })
                .ok(8080);

    }
}
