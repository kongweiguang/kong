package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.client.pipeline.step.*;
import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import io.github.kongweiguang.http.client.spec.ReqSpec;
import io.github.kongweiguang.http.client.spec.SseReqSpec;
import io.github.kongweiguang.http.client.spec.WsReqSpec;
import okhttp3.Request;

import java.util.List;

/**
 * 固定顺序流水线，确保请求组装行为确定。
 *
 * @author kongweiguang
 */
public final class RequestPipeline<S extends ReqSpec<?, ?>> {

    /**
     * 保存 steps list 数据
     */
    private final List<RequestBuildStep<S>> steps;

    private RequestPipeline(List<RequestBuildStep<S>> steps) {
        this.steps = steps;
    }

    public static RequestPipeline<HttpReqSpec> http() {
        return new RequestPipeline<>(List.of(
                new MethodBodyStep<>(),
                new ContentTypeStep<>(),
                new CookieStep<>(),
                new TagStep<>(),
                new BuildRequestStep<>()
        ));
    }

    public static RequestPipeline<SseReqSpec> sse() {
        return new RequestPipeline<>(List.of(
                new MethodBodyStep<>(),
                new ContentTypeStep<>(),
                new CookieStep<>(),
                new TagStep<>(),
                new BuildRequestStep<>()
        ));
    }

    public static RequestPipeline<WsReqSpec> ws() {
        return new RequestPipeline<>(List.of(
                new CookieStep<>(),
                new TagStep<>(),
                new BuildRequestStep<>()
        ));
    }


    /**
     * 构建 RequestPipeline instance
     */
    public Request build(S spec) {
        RequestBuildContext<S> context = new RequestBuildContext<>(spec);
        for (RequestBuildStep<S> step : steps) {
            step.apply(context);
        }
        return context.request();
    }
}

