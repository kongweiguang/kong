package io.github.kongweiguang.http.client.v2.pipeline;

import okhttp3.HttpUrl;

import static java.util.Objects.isNull;

/**
 * 最终步骤：组装 URL、请求头和 Method 生成 Request。
 */
public final class BuildRequestStep implements RequestBuildStep {
    @Override
    public void apply(RequestBuildContext context) {
        HttpUrl parsed = HttpUrl.parse(context.spec().url());
        if (isNull(parsed)) {
            throw new IllegalArgumentException("非法 URL: " + context.spec().url());
        }
        HttpUrl.Builder ub = parsed.newBuilder();
        context.spec().query().forEach((k, v) -> ub.addQueryParameter(k, String.valueOf(v)));
        context.spec().encodedQuery().forEach((k, v) -> ub.addEncodedQueryParameter(k, String.valueOf(v)));
        if (context.spec().fragment() != null) ub.fragment(context.spec().fragment());
        if (context.spec().encodedFragment() != null) ub.encodedFragment(context.spec().encodedFragment());
        context.spec().headers().forEach(context.builder()::header);

        context.request(
                context.builder()
                        .method(context.spec().method().name(), context.requestBody())
                        .url(ub.build())
                        .build()
        );
    }
}


