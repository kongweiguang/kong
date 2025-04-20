package io.github.kongweiguang.core.threads;

import java.util.concurrent.*;

/**
 * 线程池工具
 *
 * @author kongweiguang
 */
public class ThreadPools {

    // 默认线程池
    public static ExecutorService pool = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            Runtime.getRuntime().availableProcessors() * 2,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );
    //虚拟线程池
    public static ExecutorService virtualPool = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("kong-virtual-",0).factory()
    );


}
