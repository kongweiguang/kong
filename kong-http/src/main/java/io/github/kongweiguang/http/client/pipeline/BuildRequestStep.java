package io.github.kongweiguang.http.client.pipeline;

import okhttp3.HttpUrl;

/**
 * 最终步骤：组装 URL、请求头和 Method 生成 Request。
 *
 * @author kongweiguang
 */
public final class BuildRequestStep implements RequestBuildStep {

    /**
     * 组装最终 Request 对象。
     */
    @Override
    public void apply(RequestBuildContext context) {
        HttpUrl.Builder ub = context.spec().urlBuilder();

        context.spec().query().forEach((k, v) -> ub.addQueryParameter(k, String.valueOf(v)));

        context.spec().encodedQuery().forEach((k, v) -> ub.addEncodedQueryParameter(k, String.valueOf(v)));

        if (context.spec().fragment() != null) {
            ub.fragment(context.spec().fragment());
        }

        if (context.spec().encodedFragment() != null) {
            ub.encodedFragment(context.spec().encodedFragment());
        }

        context.spec().headers().forEach(context.builder()::header);

        context.request(
                context.builder()
                        .method(context.spec().method().name(), context.requestBody())
                        .url(ub.build())
                        .build()
        );
    }
}

