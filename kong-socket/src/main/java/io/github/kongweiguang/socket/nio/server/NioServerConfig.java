package io.github.kongweiguang.socket.nio.server;

/**
 * NIO服务器配置类
 * 提供服务器各种参数的配置
 *
 * @author kongweiguang
 */
public class NioServerConfig {

    // Boss线程数量，默认为CPU核心数
    private int bossThreads = Runtime.getRuntime().availableProcessors();

    // Worker线程数量，默认为CPU核心数的2倍
    private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;

    // 缓冲区大小，默认4KB
    private int bufferSize = 4 * 1024;


    private NioServerConfig() {
    }

    public static NioServerConfig of() {
        return new NioServerConfig();
    }

    /**
     * 设置Boss线程数量
     *
     * @param bossThreads Boss线程数量
     * @return 当前配置实例
     */
    public NioServerConfig bossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
        return this;
    }

    /**
     * 获取Boss线程数量
     *
     * @return Boss线程数量
     */
    public int bossThreads() {
        return bossThreads;
    }

    /**
     * 设置Worker线程数量
     *
     * @param workerThreads Worker线程数量
     * @return 当前配置实例
     */
    public NioServerConfig workerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
        return this;
    }

    /**
     * 获取Worker线程数量
     *
     * @return Worker线程数量
     */
    public int workerThreads() {
        return workerThreads;
    }

    /**
     * 设置缓冲区大小
     *
     * @param bufferSize 缓冲区大小（字节）
     * @return 当前配置实例
     */
    public NioServerConfig bufferSize(int bufferSize) {
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
