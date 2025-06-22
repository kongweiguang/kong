package io.github.kongweiguang.http.server.staticFile;

import io.github.kongweiguang.http.server.KongHttpServer;

import java.util.concurrent.Executors;

public class Test2 {
    public static void main(String[] args) {
        // 创建带高级选项的静态文件服务器
        KongHttpServer.of()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                // 高级用法 - 自定义缓存和索引文件
                .web("/static","C:\\dev\\test","index.html")
                // 设置自定义错误页面
                .errorPage(404, "<html><body><h1>404 - 页面未找到</h1><p>请检查URL是否正确</p></body></html>")
                .ok(8081);
    }
}
