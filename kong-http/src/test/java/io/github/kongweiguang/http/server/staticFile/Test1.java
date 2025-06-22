package io.github.kongweiguang.http.server.staticFile;

import io.github.kongweiguang.http.server.KongHttpServer;

import java.util.concurrent.Executors;

public class Test1 {
    public static void main(String[] args) {
            // 创建一个简单的静态文件服务器
            KongHttpServer.of()
                    .executor(Executors.newVirtualThreadPerTaskExecutor())
                    // 基础用法 - 提供静态文件目录
                    .web("/static","C:\\dev\\test","index.html")
                    .ok(8080);
    }
}
