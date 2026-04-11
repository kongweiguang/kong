package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.client.consts.Header;
import io.github.kongweiguang.http.client.utils.HttpClientUtil;

/**
 * Cookie 请求头处理步骤。
 *
 * @author kongweiguang
 */
public final class CookieStep implements RequestBuildStep {

    /**
     * 执行 apply 操作
     */
    @Override
    public void apply(RequestBuildContext context) {
        if (context.spec().cookies().isEmpty()) {
            return;
        }

        context.builder().header(Header.COOKIE.v(), HttpClientUtil.cookie2Str(context.spec().cookies()));
    }
}

