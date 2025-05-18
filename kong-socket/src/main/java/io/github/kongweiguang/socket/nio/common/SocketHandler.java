package io.github.kongweiguang.socket.nio.common;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Socket处理接口
 * 用于处理服务器响应
 *
 * @author kongweiguang
 */
public interface SocketHandler {

    /**
     * 处理响应数据
     *
     * @param response 服务器返回的响应数据
     * @param channel  Socket通道
     */
    ByteBuffer handler(ByteBuffer buffer, SocketChannel channel);
}
