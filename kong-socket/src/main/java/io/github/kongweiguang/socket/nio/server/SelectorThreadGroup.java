package io.github.kongweiguang.socket.nio.server;

import io.github.kongweiguang.core.utils.IoUtil;
import io.github.kongweiguang.core.lang.Strs;
import io.github.kongweiguang.socket.nio.common.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 选择器线程组类，负责管理多个选择器线程
 * 可以配置为主线程组(boss)和工作线程组(worker)
 *
 * @author kongweiguang
 */
public class SelectorThreadGroup implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(SelectorThreadGroup.class);

    // 选择器线程数组
    private SelectorThread[] selectorThreads;

    // 线程计数器，用于轮询分配
    private final AtomicInteger counter = new AtomicInteger(0);

    // 工作线程组，默认为自身
    private SelectorThreadGroup worker;

    // 缓冲区大小，默认4KB
    private int bufferSize;

    // 请求处理器
    private SocketHandler requestHandler;

    /**
     * 创建选择器线程组
     *
     * @param num        线程数量
     * @param bufferSize 缓冲区大小（字节）
     * @return SelectorThreadGroup
     */
    public static SelectorThreadGroup of(int num, int bufferSize) {
        return new SelectorThreadGroup(num, bufferSize);
    }

    /**
     * 创建选择器线程组
     *
     * @param num        线程数量
     * @param bufferSize 缓冲区大小（字节）
     */
    private SelectorThreadGroup(int num, int bufferSize) {
        this.bufferSize = bufferSize;
        init(num);
    }


    /**
     * 初始化选择器线程组
     *
     * @param num 线程数量
     */
    private void init(int num) {
        selectorThreads = new SelectorThread[num];
        for (int i = 0; i < num; i++) {
            selectorThreads[i] = SelectorThread.of(this);
            Thread thread = new Thread(selectorThreads[i]);
            thread.setName("nio-selector-thread-" + i);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * 设置工作线程组
     *
     * @param worker 工作线程组
     * @return 当前选择器线程组
     */
    public SelectorThreadGroup worker(SelectorThreadGroup worker) {
        this.worker = worker;
        return this;
    }

    /**
     * 设置请求处理器
     *
     * @param requestHandler 请求处理器实例
     * @return 当前选择器线程组
     */
    public SelectorThreadGroup socketHandler(SocketHandler requestHandler) {
        this.requestHandler = requestHandler;
        return this;
    }

    /**
     * 获取请求处理器
     *
     * @return 当前配置的请求处理器
     */
    public SocketHandler requestHandler() {
        return this.requestHandler;
    }

    /**
     * 设置缓冲区大小
     *
     * @param bufferSize 缓冲区大小（字节）
     * @return 当前选择器线程组
     */
    public SelectorThreadGroup bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    /**
     * 获取缓冲区大小
     *
     * @return 缓冲区大小（字节）
     */
    public int bufferSize() {
        return this.bufferSize;
    }

    /**
     * 绑定服务器地址和端口
     *
     * @param address 地址
     * @return 当前选择器线程组
     */
    public SelectorThreadGroup bind(InetSocketAddress address) {
        try {
            // 服务器套接字通道
            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(address);
            nextSelector(server);
            log.info("server started at port: {}:{} with {} threads.", address.getHostName(), address.getPort(), selectorThreads.length);

        } catch (IOException e) {
            throw new RuntimeException(Strs.fmt("bind  fail to {}:{}",address.getHostName(),address.getPort()) , e);
        }

        return this;
    }

    /**
     * 将通道分配给下一个选择器处理
     *
     * @param channel 要分配的通道
     */
    public void nextSelector(Channel channel) {
        try {
            SelectorThread selectorThread;
            if (channel instanceof ServerSocketChannel) {
                // 服务器通道由boss线程组处理
                selectorThread = next();
            } else {
                // 客户端通道由worker线程组处理
                selectorThread = nextWorker();
            }
            selectorThread.queue().put(channel);
            // 唤醒选择器
            selectorThread.selector().wakeup();
        } catch (Exception e) {
            log.error("next selector error", e);
        }
    }

    /**
     * 获取下一个选择器线程（轮询）
     *
     * @return 选择器线程实例
     */
    private SelectorThread next() {
        int index = counter.getAndIncrement() % selectorThreads.length;
        return selectorThreads[index];
    }

    /**
     * 获取下一个工作选择器线程（轮询）
     *
     * @return 工作选择器线程实例
     */
    private SelectorThread nextWorker() {
        // 使用位运算优化，要求worker线程组大小为2的幂
        int index = counter.getAndIncrement() & (worker.selectorThreads.length - 1);
        return worker.selectorThreads[index];
    }

    @Override
    public void close() throws Exception {
        for (SelectorThread selectorThread : selectorThreads) {
            IoUtil.close(selectorThread);
        }
    }
}