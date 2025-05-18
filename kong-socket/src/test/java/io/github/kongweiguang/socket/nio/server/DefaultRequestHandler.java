package io.github.kongweiguang.socket.nio.server;

import io.github.kongweiguang.socket.nio.common.SocketHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 默认请求处理器实现
 * 提供基本的HTTP响应
 */
public class DefaultRequestHandler implements SocketHandler {

    /**
     * HTTP响应内容
     */
    private final String responseContent;

    /**
     * 创建默认响应处理器
     */
    public DefaultRequestHandler() {
        this("Hello World!");
    }

    /**
     * 创建自定义内容的响应处理器
     *
     * @param content 响应内容
     */
    public DefaultRequestHandler(String content) {
        this.responseContent = content;
    }

    @Override
    public ByteBuffer handler(ByteBuffer buffer, SocketChannel channel) {
        // 简单的HTTP响应
        String response = "HTTP/1.1 200 OK\r\n" +
                          "Content-Length: " + responseContent.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                          "Content-Type: text/plain\r\n" +
                          "\r\n" +
                          responseContent;

        System.out.println("response = " + response);
        return ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
    }
}
