package io.github.kongweiguang.http.client.pipeline.step;

import io.github.kongweiguang.http.client.pipeline.RequestBuildContext;
import io.github.kongweiguang.http.client.spec.ReqSpec;
import okhttp3.HttpUrl;

/**
 * 最终步骤：组装 URL、请求头和 Method 生成 Request。
 *
 * @author kongweiguang
 */
public final class BuildRequestStep<S extends ReqSpec<?, ?>> implements RequestBuildStep<S> {


    /**
     * 组装最终 Request 对象。
     */
    @Override
    public void apply(RequestBuildContext<S> context) {
        HttpUrl.Builder ub = context.spec().urlBuilder();

        context.request(
                context.spec()
                        .builder()
                        .method(context.spec().method().name(), context.requestBody())
                        .url(ub.build())
                        .build()
        );
    }
}

