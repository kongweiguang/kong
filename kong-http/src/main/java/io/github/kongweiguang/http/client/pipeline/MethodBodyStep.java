package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.client.body.BodyEncoderFactory;
import io.github.kongweiguang.http.client.consts.Method;
import okhttp3.RequestBody;

import static io.github.kongweiguang.core.lang.Opt.ofNullable;

/**
 * 在组装请求前应用 method/body 兼容规则。
 *
 * @author kongweiguang
 */
public final class MethodBodyStep implements RequestBuildStep {

    /**
     * 根据 HTTP method 决定请求体处理策略。
     */
    @Override
    public void apply(RequestBuildContext context) {
        RequestBody body = BodyEncoderFactory.resolve(context.spec()).encode(context.spec());

        if (context.spec().method() == Method.GET || context.spec().method() == Method.HEAD) {
            context.requestBody(null);
            return;
        }

        if (context.spec().method() == Method.POST
            || context.spec().method() == Method.PUT
            || context.spec().method() == Method.PATCH) {
            context.requestBody(ofNullable(body).orElse(RequestBody.create(new byte[0])));
            return;
        }

        context.requestBody(body);
    }
}

