package io.github.kongweiguang.socket.nio.client;

import io.github.kongweiguang.socket.nio.common.SocketHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 默认客户端响应处理器
 * 提供基本的响应处理
 */
public class DefaultClientHandler implements SocketHandler {

    @Override
    public ByteBuffer handler(ByteBuffer response, SocketChannel channel) {
        byte[] data = new byte[response.remaining()];
        response.get(data);
        String responseStr = new String(data, StandardCharsets.UTF_8);
        System.out.println("收到响应: " + responseStr);
        return response;
    }


}
