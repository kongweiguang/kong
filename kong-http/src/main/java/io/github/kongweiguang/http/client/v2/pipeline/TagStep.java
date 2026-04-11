package io.github.kongweiguang.http.client.v2.pipeline;

import io.github.kongweiguang.http.client.HttpRequestSpec;

/**
 * Tags request with the immutable spec so listeners can recover request context.
 */
public final class TagStep implements RequestBuildStep {
    @Override
    public void apply(RequestBuildContext context) {
        context.builder().tag(HttpRequestSpec.class, context.spec());
    }
}

