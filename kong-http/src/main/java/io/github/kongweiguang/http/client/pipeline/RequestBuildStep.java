package io.github.kongweiguang.http.client.pipeline;

/**
 * 请求构建流水线中的单个步骤。
 *
 * @author kongweiguang
 */
public interface RequestBuildStep {

    void apply(RequestBuildContext context);
}

