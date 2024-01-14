package io.github.kongweiguang.bus;


import io.github.kongweiguang.bus.core.DefaultHubImpl;
import io.github.kongweiguang.bus.core.Hub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类似git操作的本地eventBus
 *
 * @author kongweiguang
 */
@SuppressWarnings("all")
public final class Bus {
    private Bus() {
        throw new UnsupportedOperationException("bus not must be construct");
    }

    private static final Map<String, Hub<?, ?>> hubs = new ConcurrentHashMap<>();
    private static final Hub<?, ?> hub = new DefaultHubImpl<>();

    /**
     * 默认的hub
     *
     * @param <C> 操作的内容类型
     * @param <R> 返回的结果类型
     * @return hub
     */
    public static <C, R> Hub<C, R> hub() {
        return (Hub<C, R>) hub;
    }

    /**
     * 自定义hub
     *
     * @param name hub的名称
     * @param <C>  操作的内容类型
     * @param <R>  返回的结果类型
     * @return hub
     */
    public static <C, R> Hub<C, R> hub(final String name) {
        return (Hub<C, R>) hubs.computeIfAbsent(name, k -> new DefaultHubImpl<>());
    }
}
