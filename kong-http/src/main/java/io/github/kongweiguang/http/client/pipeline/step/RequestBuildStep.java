package io.github.kongweiguang.http.client.pipeline.step;

import io.github.kongweiguang.http.client.pipeline.RequestBuildContext;
import io.github.kongweiguang.http.client.spec.ReqSpec;

/**
 * 请求构建流水线中的单个步骤。
 *
 * @author kongweiguang
 */
public interface RequestBuildStep<S extends ReqSpec<?, ?>> {

    void apply(RequestBuildContext<S> context);
}

