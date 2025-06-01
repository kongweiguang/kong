package io.github.kongweiguang.http.server;


import io.github.kongweiguang.core.threads.Threads;
import io.github.kongweiguang.http.client.sse.SseEvent;
import io.github.kongweiguang.http.common.exception.KongHttpRuntimeException;
import io.github.kongweiguang.http.server.core.HttpReq;
import io.github.kongweiguang.http.server.core.HttpRes;
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
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                //设置静态web地址，默认寻找index.html
                .web("/static", "C:\\dev\\js\\xm\\vite-dev\\dist", "index.html")
                .web("/assets", "C:\\dev\\js\\xm\\vite-dev\\dist\\assets", "")
                .get("/get", (req, res) -> {
                    res.send("ok");
                })
                .get("/get_string", (req, res) -> {
                    System.out.println("req = " + req.query());
                    System.out.println("req = " + req.params());
                    res.send("ok");
                })
                .post("/post_json", (req, res) -> {
                    Map<String, List<String>> params = req.params();
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
                    Map<String, List<String>> headers = req.headerMap();
                    System.out.println("headers = " + headers);
                    res.send("ok");
                })
                //接受post请求
                .post("/post_body", ((req, res) -> {
                    String str = req.str();
                    System.out.println("str = " + str);
                    res.send(str);
                }))
                .post("/post_form", ((req, res) -> {
                    System.out.println(req.params());
                    res.send("ok");
                }))
                //上传
                .post("/post_mul_form", (req, res) -> {
                    Map<String, List<String>> params = req.params();
                    System.out.println("params = " + params);
                    Map<String, List<UploadFile>> files = req.fileMap();
                    System.out.println("files = " + files);
                    res.send("ok");
                })
                .get("/error", ((req, res) -> {
                    System.out.println("req.str() = " + req.str());

                    throw new KongHttpRuntimeException("error");
                }))
                .get("/timeout", ((req, res) -> {
                    Threads.sleep(5000);
                    res.send("ok");
                }))
                //下载文件
                .get("/xz", (req, res) ->
                        res.file("k.txt", Files.readAllBytes(Paths.get("C:\\test\\test.txt"))))
                //sse响应
                .sse("/sse", new SSEHandler() {
                    @Override
                    public void handler(HttpReq req, HttpRes res) {
                        for (int i = 0; i < 10; i++) {
                            Threads.sleep(500);
                            send(SseEvent.of()
                                    .id(UUID.randomUUID().toString())
                                    .type("eventType")
                                    .data(new Date().toString())
                            );
                        }

                        //完成
                        send(SseEvent.of()
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
