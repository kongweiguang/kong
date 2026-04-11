package io.github.kongweiguang.http.client.core;

import okhttp3.OkHttpClient;

/**
 * 将单个配置项应用到 OkHttp 构建器。
 */
@FunctionalInterface
public interface ConfApplier {
    void apply(Conf conf, OkHttpClient.Builder builder);
}


