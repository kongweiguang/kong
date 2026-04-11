package io.github.kongweiguang.http.client.v2.pipeline;

import io.github.kongweiguang.http.common.core.Header;
import io.github.kongweiguang.http.common.utils.HttpClientUtil;

/**
 * Writes cookie header only when cookies are present.
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

