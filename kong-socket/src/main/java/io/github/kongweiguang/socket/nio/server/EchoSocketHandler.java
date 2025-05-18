package io.github.kongweiguang.socket.nio.server;

import io.github.kongweiguang.socket.nio.common.SocketHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 回显请求处理器实现
 * 将接收到的数据直接返回给客户端
 *
 * @author kongweiguang
 */
public class EchoSocketHandler implements SocketHandler {

    @Override
    public ByteBuffer handler(ByteBuffer buffer, SocketChannel channel) {
        return buffer;
    }
}
