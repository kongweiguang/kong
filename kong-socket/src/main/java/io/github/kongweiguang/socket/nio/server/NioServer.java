package io.github.kongweiguang.socket.nio.server;

import io.github.kongweiguang.core.lang.IOs;
import io.github.kongweiguang.socket.nio.common.SocketHandler;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * NIO服务器工具类
 * 提供简单的API来创建和配置NIO服务器
 *
 * @author kongweiguang
 */
public class NioServer implements AutoCloseable {

    // Boss线程组 - 负责接受连接
    private SelectorThreadGroup bossGroup;

    // Worker线程组 - 负责处理I/O
    private SelectorThreadGroup workerGroup;

    // 配置信息
    private NioServerConfig config;

    private NioServer() {
    }

    /**
     * 创建NIO服务器
     *
     * @param config 服务器配置
     */
    private NioServer(NioServerConfig config) {
        this.config = config;

        // 创建boss线程组
        this.bossGroup = SelectorThreadGroup.of(config.bossThreads(), config.bufferSize());

        // 创建worker线程组
        this.workerGroup = SelectorThreadGroup.of(config.workerThreads(), config.bufferSize());

        // 设置worker线程组
        bossGroup.worker(workerGroup);

        // 默认使用回显处理器
        workerGroup.socketHandler(new EchoSocketHandler());
    }

    /**
     * 创建NIO服务器
     *
     * @param configConsumer 配置消费者
     * @return NioServer
     */
    public static NioServer of(Consumer<NioServerConfig> configConsumer) {
        NioServerConfig config = NioServerConfig.of();
        configConsumer.accept(config);
        return new NioServer(config);
    }


    /**
     * 创建NIO服务器
     *
     * @return NioServer
     */
    public static NioServer of() {
        return new NioServer(NioServerConfig.of());
    }

    /**
     * 配置通道处理
     *
     * @param handler 通道处理器
     * @return 当前服务器实例
     */
    public NioServer socketHandler(SocketHandler handler) {
        workerGroup.socketHandler(handler);
        return this;
    }

    /**
     * 绑定服务器端口
     *
     * @param port 要绑定的端口
     * @return 当前服务器实例
     */
    public NioServer bind(int port) {
        bossGroup.bind(new InetSocketAddress(port));
        return this;
    }

    /**
     * 绑定服务器地址和端口
     *
     * @param host 要绑定的主机地址
     * @param port 要绑定的端口
     * @return 当前服务器实例
     */
    public NioServer bind(String host, int port) {
        bossGroup.bind(new InetSocketAddress(host, port));
        return this;
    }

    /**
     * 获取Boss线程组
     *
     * @return Boss线程组实例
     */
    public SelectorThreadGroup bossGroup() {
        return bossGroup;
    }

    /**
     * 获取Worker线程组
     *
     * @return Worker线程组实例
     */
    public SelectorThreadGroup workerGroup() {
        return workerGroup;
    }

    /**
     * 获取服务器配置
     *
     * @return 服务器配置实例
     */
    public NioServerConfig config() {
        return config;
    }

    @Override
    public void close() throws Exception {
        IOs.close(bossGroup);
        IOs.close(workerGroup);
    }
}
