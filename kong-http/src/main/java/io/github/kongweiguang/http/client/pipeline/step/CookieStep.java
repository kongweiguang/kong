package io.github.kongweiguang.http.client.pipeline.step;

import io.github.kongweiguang.http.client.consts.Header;
import io.github.kongweiguang.http.client.pipeline.RequestBuildContext;
import io.github.kongweiguang.http.client.spec.ReqSpec;

import static io.github.kongweiguang.http.client.utils.HttpClientUtil.cookie2Str;

/**
 * Cookie 请求头处理步骤。
 *
 * @author kongweiguang
 */
public final class CookieStep<S extends ReqSpec<?, ?>> implements RequestBuildStep<S> {

    /**
     * 执行 apply 操作
     */
    @Override
    public void apply(RequestBuildContext<S> context) {
        if (context.spec().cookies().isEmpty()) {
            return;
        }

        context.spec().builder().header(Header.COOKIE.value(), cookie2Str(context.spec().cookies()));
    }
}

