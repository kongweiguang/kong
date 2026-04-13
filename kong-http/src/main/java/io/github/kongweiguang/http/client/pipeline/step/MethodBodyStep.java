package io.github.kongweiguang.http.client.pipeline.step;

import io.github.kongweiguang.http.client.body.BodyEncoderFactory;
import io.github.kongweiguang.http.client.consts.Method;
import io.github.kongweiguang.http.client.pipeline.RequestBuildContext;
import io.github.kongweiguang.http.client.spec.ReqSpec;
import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static io.github.kongweiguang.core.lang.Opt.ofNullable;

/**
 * 在组装请求前应用 method/body 兼容规则。
 *
 * @author kongweiguang
 */
public final class MethodBodyStep<S extends ReqSpec<?, ?>> implements RequestBuildStep<S> {

    /**
     * 根据 HTTP method 决定请求体处理策略。
     */
    @Override
    public void apply(RequestBuildContext<S> context) {
        RequestBody body = resolveBody(context.spec());

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

    private RequestBody resolveBody(S spec) {
        if (spec instanceof HttpReqSpec httpSpec) {
            return BodyEncoderFactory.resolve(httpSpec).encode(httpSpec);
        }
        if (spec.body() == null) {
            return null;
        }
        return RequestBody.create(MediaType.parse(spec.contentType()), spec.body());
    }
}

