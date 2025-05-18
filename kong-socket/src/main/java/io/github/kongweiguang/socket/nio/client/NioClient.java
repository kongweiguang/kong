package io.github.kongweiguang.socket.nio.client;

import io.github.kongweiguang.core.lang.IOs;
import io.github.kongweiguang.core.threads.ThreadPools;
import io.github.kongweiguang.socket.nio.common.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * NIO客户端实现
 * 提供异步非阻塞的Socket通信能力
 *
 * @author kongweiguang
 */
public class NioClient implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(NioClient.class);
    //客户端配置
    private final NioClientConfig config;

    //客户端Socket处理器
    private SocketHandler socketHandler;

    //选择器
    private final Selector selector;

    private SocketChannel socketChannel;

    //是否运行中
    private volatile boolean running = false;

    /**
     * 创建客户端
     *
     * @param config 配置
     * @return 客户端实例
     */
    public static NioClient of(NioClientConfig config) {
        return new NioClient(config);
    }

    /**
     * 使用配置器创建客户端
     *
     * @param configurer 配置器
     * @return 客户端实例
     */
    public static NioClient of(Consumer<NioClientConfig> configurer) {
        NioClientConfig config = NioClientConfig.of();
        configurer.accept(config);
        return new NioClient(config);
    }

    /**
     * 创建默认配置的客户端
     *
     * @return 客户端实例
     */
    public static NioClient of() {
        return new NioClient(NioClientConfig.of());
    }

    /**
     * 构造方法
     *
     * @param config 配置
     */
    private NioClient(NioClientConfig config) {
        this.config = config;
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException("init client fail ", e);
        }
    }

    /**
     * 设置Socket处理器
     *
     * @param socketHandler 处理器
     * @return 客户端实例
     */
    public NioClient socketHandler(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
        return this;
    }

    /**
     * 连接服务器
     *
     * @param host 主机地址
     * @param port 端口
     * @return 客户端实例
     */
    public NioClient connect(String host, int port) {
        try {
            this.socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(host, port));
            socketChannel.register(selector, SelectionKey.OP_READ);

            while (!socketChannel.finishConnect()) {
            }

            if (!running) {
                running = true;
                startEventLoop();
            }

            return this;
        } catch (IOException e) {
            throw new RuntimeException("connection fail to : " + host + ":" + port, e);
        }
    }

    /**
     * 异步发送请求并获取响应
     *
     * @param request 请求数据
     * @return 异步响应结果
     */
    public NioClient send(ByteBuffer request) {
        try {
            socketChannel.write(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    /**
     * 发送字符串请求
     *
     * @param msg 请求字符串
     * @return 异步响应结果
     */
    public NioClient send(String msg) {
        return send(ByteBuffer.wrap(msg.getBytes()));
    }

    /**
     * 启动事件循环
     */
    private void startEventLoop() {
        ThreadPools.virtualPool.execute(() -> {
            try {
                while (running && selector.isOpen() && selector.select() != 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();

                        if (key.isReadable()) {
                            handleRead(key);
                        }

                        iterator.remove();

                    }
                }
            } catch (Exception e) {
                log.error("nio client event loop error", e);
            }
        });
    }


    /**
     * 处理读取事件
     *
     * @param key 选择键
     * @throws IOException IO异常
     */
    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(config.bufferSize());

        int bytesRead = channel.read(buffer);
        if (bytesRead > 0) {
            buffer.flip();
            // 调用处理器处理响应
            if (socketHandler != null) {
                socketHandler.handler(buffer, channel);
            }

        } else if (bytesRead < 0) {
            close();
            log.info("nio client connection closed");
        }
    }


    @Override
    public void close() {
        this.running = false;
        IOs.close(selector);
        IOs.close(socketChannel);
    }
}
