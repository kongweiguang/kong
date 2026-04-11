package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.Request;

import java.util.List;

/**
 * 固定顺序流水线，确保请求组装行为确定。
 */
public final class RequestPipeline {
    private final List<RequestBuildStep> steps = List.of(
            new MethodBodyStep(),
            new ContentTypeStep(),
            new CookieStep(),
            new TagStep(),
            new BuildRequestStep()
    );

    /**
     * 构建并返回最终结果。
     */
    public Request build(HttpRequestSpec spec) {
        RequestBuildContext context = new RequestBuildContext(spec);
        for (RequestBuildStep step : steps) {
            step.apply(context);
        }
        return context.request();
    }
}


