package io.github.kongweiguang.http.client.v2.pipeline;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.Request;

import java.util.List;

/**
 * Fixed-order pipeline for deterministic request assembly.
 */
public final class RequestPipeline {
    private final List<RequestBuildStep> steps = List.of(
            new MethodBodyStep(),
            new ContentTypeStep(),
            new CookieStep(),
            new TagStep(),
            new BuildRequestStep()
    );

    public Request build(HttpRequestSpec spec) {
        RequestBuildContext context = new RequestBuildContext(spec);
        for (RequestBuildStep step : steps) {
            step.apply(context);
        }
        return context.request();
    }
}

