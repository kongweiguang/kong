package io.github.kongweiguang.socket.nio.server;

import java.util.concurrent.locks.LockSupport;

/**
 * NIO服务器示例类
 * 展示如何使用NioServer工具类创建和配置NIO服务器
 */
public class ServerTest {

    public static void main(String[] args) {
        // 创建自定义配置
        NioServerConfig config = NioServerConfig.of()
                .bossThreads(1)
                .workerThreads(4)
                .bufferSize(8192);

        // 创建服务器实例
        NioServer.of(config)
//                .socketHandler(new DefaultRequestHandler())
                .socketHandler(new EchoSocketHandler())
                .bind(8888)
                .bind("localhost", 8887);

        System.out.println("服务器已启动，监听端口: 8888, 8887");
        LockSupport.park();
    }

}
