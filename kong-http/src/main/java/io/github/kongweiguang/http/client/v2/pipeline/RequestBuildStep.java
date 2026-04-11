package io.github.kongweiguang.http.client.v2.pipeline;

/**
 * A single step in request build pipeline.
 */
public interface RequestBuildStep {
    void apply(RequestBuildContext context);
}

