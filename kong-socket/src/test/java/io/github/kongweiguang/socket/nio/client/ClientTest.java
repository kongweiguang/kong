package io.github.kongweiguang.socket.nio.client;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * NIO客户端示例类
 * 展示如何使用NioClient工具类创建和配置NIO客户端
 */
public class ClientTest {

    public static void main(String[] args) {
        // 创建客户端实例并连接
        NioClient client = NioClient.of()
                .socketHandler((response, channel) -> {
                    //打印响应
                    byte[] data = new byte[response.remaining()];
                    response.get(data);
                    System.out.println("收到响应: " + new String(data, StandardCharsets.UTF_8));
                    return null;
                })
                .connect("localhost", 8888);

        System.out.println("客户端已启动，连接到服务器: localhost:8888");


        // 等待连接建立
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要发送的消息:");
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (request != null && !request.trim().isEmpty()) {
                System.out.println("request = " + request);
                client.send(request);
            }
        }
    }

}
