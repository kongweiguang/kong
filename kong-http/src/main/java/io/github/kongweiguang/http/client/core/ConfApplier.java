package io.github.kongweiguang.http.client.core;

import okhttp3.OkHttpClient;

/**
 * Applies a single config concern to an OkHttp builder.
 */
@FunctionalInterface
public interface ConfApplier {
    void apply(Conf conf, OkHttpClient.Builder builder);
}

