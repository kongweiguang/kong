package io.github.kongweiguang.socket.nio.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 请求处理器接口
 * 用于处理客户端请求并返回响应
 *
 * @author kongweiguang
 */
public interface ServerSocketHandler {

    /**
     * 处理客户端请求
     *
     * @param buffer  包含请求数据的缓冲区
     * @param channel 客户端通道
     * @return 响应缓冲区，如果返回null则不发送响应
     */
    ByteBuffer handler(ByteBuffer buffer, SocketChannel channel);
}
