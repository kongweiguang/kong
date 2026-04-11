package io.github.kongweiguang.http.client.core;

import okhttp3.OkHttpClient;

/**
 * 将单个配置项应用到 OkHttp 构建器。
 *
 * @author kongweiguang
 */
@FunctionalInterface
public interface ConfApplier {
    /**
     * 将配置应用到构建器。
     *
     * @param conf    配置对象
     * @param builder OkHttp 构建器
     */
    void apply(Conf conf, OkHttpClient.Builder builder);
}

