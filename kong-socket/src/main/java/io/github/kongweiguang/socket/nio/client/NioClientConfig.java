package io.github.kongweiguang.socket.nio.client;

import java.util.function.Consumer;

/**
 * NIO客户端配置类
 * 用于配置NioClient的运行参数
 *
 * @author kongweiguang
 */
public class NioClientConfig {

    // 缓冲区大小，默认4KB
    private int bufferSize = 4 * 1024;

    /**
     * 创建默认配置
     *
     * @return 配置实例
     */
    public static NioClientConfig of() {
        return new NioClientConfig();
    }

    /**
     * 使用配置器创建配置
     *
     * @param consumer 配置器
     * @return 配置实例
     */
    public static NioClientConfig of(Consumer<NioClientConfig> consumer) {
        NioClientConfig config = new NioClientConfig();
        consumer.accept(config);
        return config;
    }

    /**
     * 设置缓冲区大小
     *
     * @param bufferSize 缓冲区大小（字节）
     * @return 配置实例
     */
    public NioClientConfig bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    /**
     * 获取缓冲区大小
     *
     * @return 缓冲区大小（字节）
     */
    public int bufferSize() {
        return bufferSize;
    }


}
