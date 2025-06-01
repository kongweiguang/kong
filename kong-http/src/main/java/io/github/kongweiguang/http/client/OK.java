package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.builder.ReqBuilder;
import io.github.kongweiguang.http.client.core.Conf;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 请求发送器抽象基类
 *
 * @param <T> 请求构建器类型
 * @param <R> 响应结果类型
 * @author kongweiguang
 */
public abstract class OK<T extends ReqBuilder<?, R>, R> {
    protected final OkHttpClient client;
    protected final T reqBuilder;

    protected OK(T reqBuilder, OkHttpClient client) {
        this.client = client;
        this.reqBuilder = reqBuilder;
    }

    /**
     * 执行请求
     *
     * @return 响应结果
     */
    protected abstract R execute();

    /**
     * 获取全局配置
     *
     * @return 全局配置
     */
    public static Conf conf() {
        return Conf.global();
    }

    /**
     * 获取OkHttpClient客户端
     *
     * @return OkHttpClient客户端
     */
    protected OkHttpClient client() {
        return client;
    }

    /**
     * 获取请求对象
     *
     * @return 请求对象
     */
    protected Request request() {
        return reqBuilder.builder().build();
    }

    /**
     * 获取请求构建器
     *
     * @return 请求构建器
     */
    protected T reqBuilder() {
        return reqBuilder;
    }
}