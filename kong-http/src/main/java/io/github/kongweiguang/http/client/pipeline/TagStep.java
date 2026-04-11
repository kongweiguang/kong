package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.client.HttpRequestSpec;

/**
 * Request 标签写入步骤。
 *
 * @author kongweiguang
 */
public final class TagStep implements RequestBuildStep {

    /**
     * 将 HttpRequestSpec 写入 Request 的 tag。
     */
    @Override
    public void apply(RequestBuildContext context) {
        context.builder().tag(HttpRequestSpec.class, context.spec());
    }
}

