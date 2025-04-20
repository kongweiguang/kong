package io.github.kongweiguang.http.server.staticFile;

import io.github.kongweiguang.http.server.JavaServer;

import java.util.concurrent.Executors;

public class Test3 {
    public static void main(String[] args) {
        // 创建带完整配置的静态文件服务器
        JavaServer.of()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                // 最高级用法 - 自定义WebHandler的所有选项
                .web("/static","C:\\dev\\test",handler ->
                        // 设置100MB最大缓存
                        handler.maxCacheSize(100 * 1024 * 1024)
                                // 启用ETag支持
                                .enableETag(true)
                                // 添加自定义MIME类型
                                .addMimeType("md", "text/markdown")
                )
                .ok(8082);
    }
}
