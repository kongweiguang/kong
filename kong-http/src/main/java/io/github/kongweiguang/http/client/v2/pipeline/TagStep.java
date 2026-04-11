package io.github.kongweiguang.http.client.v2.pipeline;

import io.github.kongweiguang.http.client.HttpRequestSpec;

/**
 * 将不可变规格写入 tag，便于监听器回取上下文。
 */
public final class TagStep implements RequestBuildStep {
    @Override
    public void apply(RequestBuildContext context) {
        context.builder().tag(HttpRequestSpec.class, context.spec());
    }
}


