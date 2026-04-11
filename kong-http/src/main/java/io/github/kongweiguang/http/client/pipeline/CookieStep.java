package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.common.core.Header;
import io.github.kongweiguang.http.common.utils.HttpClientUtil;

/**
 * 仅在存在 Cookie 时写入 Cookie 请求头。
 */
public final class CookieStep implements RequestBuildStep {
    @Override
    public void apply(RequestBuildContext context) {
        if (context.spec().cookies().isEmpty()) {
            return;
        }
        context.builder().header(Header.COOKIE.v(), HttpClientUtil.cookie2Str(context.spec().cookies()));
    }
}


