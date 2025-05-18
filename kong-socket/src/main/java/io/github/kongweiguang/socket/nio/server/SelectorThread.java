package io.github.kongweiguang.socket.nio.server;

import io.github.kongweiguang.core.lang.IOs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 选择器线程类，负责处理NIO事件
 * 每个选择器线程管理一个Selector实例，处理注册的通道事件
 *
 * @author kongweiguang
 */
public class SelectorThread extends ThreadLocal<LinkedBlockingQueue<Channel>> implements Runnable, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(SelectorThread.class);
    // 选择器实例
    private final Selector selector;
    // 通道队列，用于存储待处理的通道
    private final LinkedBlockingQueue<Channel> queue = get();
    // 所属的选择器线程组
    private final SelectorThreadGroup selectorThreadGroup;

    private volatile boolean running = false;


    /**
     * 构造选择器线程
     *
     * @param selectorThreadGroup 所属的选择器线程组
     */
    private SelectorThread(SelectorThreadGroup selectorThreadGroup) {
        try {
            this.selectorThreadGroup = selectorThreadGroup;
            this.selector = Selector.open();

            if (!running) {
                this.running = true;
            }
        } catch (IOException e) {
            throw new RuntimeException("create selector fail ", e);
        }
    }

    /**
     * 创建选择器线程
     *
     * @param selectorThreadGroup 所属的选择器线程组
     * @return 选择器线程
     */
    public static SelectorThread of(SelectorThreadGroup selectorThreadGroup) {
        return new SelectorThread(selectorThreadGroup);
    }

    @Override
    protected LinkedBlockingQueue<Channel> initialValue() {
        return new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        while (running) {
            try {
                // 阻塞选择就绪的通道
                int nums = selector.select();

                // 处理已就绪的事件
                if (nums > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        // 根据事件类型进行处理
                        if (key.isAcceptable()) {
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHandler(key);
                        }
                    }
                }

                // 处理队列中的通道注册事件
                if (!queue.isEmpty()) {
                    Channel channel = queue.take();

                    if (channel instanceof ServerSocketChannel server) {
                        // 注册接受连接事件
                        server.register(selector, SelectionKey.OP_ACCEPT);
                    } else if (channel instanceof SocketChannel client) {
                        ByteBuffer buffer = ByteBuffer.allocateDirect(selectorThreadGroup.bufferSize());
                        // 注册可读事件
                        client.register(selector, SelectionKey.OP_READ, buffer);
                    }
                }
            } catch (IOException | InterruptedException e) {
                log.error("nio selector thread error", e);
            }
        }
    }

    /**
     * 处理读事件
     *
     * @param key 选择键
     */
    private void readHandler(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(selectorThreadGroup.bufferSize());
            key.attach(buffer);
        }

        try {
            // 读取数据
            int bytesRead = socketChannel.read(buffer);

            if (bytesRead == -1) {
                // 连接已关闭
                key.cancel();
                socketChannel.close();
                return;
            }

            // 处理请求
            if (bytesRead > 0) {
                // 准备读取数据
                buffer.flip();
                ByteBuffer response = selectorThreadGroup.requestHandler().handler(buffer, socketChannel);
                socketChannel.write(response);
                // 清空缓冲区，准备下次读取
                buffer.clear();
            }
        } catch (IOException e) {
            // 处理连接异常
            try {
                key.cancel();
                socketChannel.close();
            } catch (IOException ex) {
                log.error("close socket channel error", ex);
            }
        }
    }

    /**
     * 处理接受连接事件
     *
     * @param key 选择键
     */
    private void acceptHandler(SelectionKey key) {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            // 分配给下一个选择器处理
            selectorThreadGroup.nextSelector(client);
        } catch (IOException e) {
            log.error("accept socket channel error", e);
        }
    }

    /**
     * @return
     */
    public Selector selector() {
        return selector;
    }

    /**
     * @return
     */
    public LinkedBlockingQueue<Channel> queue() {
        return queue;
    }

    /**
     * @return
     */
    public SelectorThreadGroup selectorThreadGroup() {
        return selectorThreadGroup;
    }

    @Override
    public void close() throws Exception {
        this.running = false;
        IOs.close(selector);
    }
}