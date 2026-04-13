package io.github.kongweiguang.http.client.pipeline.step;

import io.github.kongweiguang.http.client.pipeline.RequestBuildContext;
import io.github.kongweiguang.http.client.spec.ReqSpec;

/**
 * Request 标签写入步骤。
 *
 * @author kongweiguang
 */
public final class TagStep<S extends ReqSpec<?, ?>> implements RequestBuildStep<S> {

    /**
     * 将 ReqSpec 写入 Request 的 tag。
     */
    @Override
    @SuppressWarnings({"rawtypes"})
    public void apply(RequestBuildContext<S> context) {
        ReqSpec spec = context.spec();
        context.spec().builder().tag(ReqSpec.class, spec);
    }
}
