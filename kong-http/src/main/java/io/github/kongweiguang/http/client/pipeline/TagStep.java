package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.client.HttpRequestSpec;

public final class TagStep implements RequestBuildStep {

    @Override
    public void apply(RequestBuildContext context) {
        context.builder().tag(HttpRequestSpec.class, context.spec());
    }
}

