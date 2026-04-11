package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Header;

/**
 * 为非 form/non-multipart 请求体补充标准化 Content-Type。
 */
public final class ContentTypeStep implements RequestBuildStep {
    @Override
    /**
     * 对上下文执行当前步骤处理。
     */
    public void apply(RequestBuildContext context) {
        String ct = context.spec().contentType();
        if (ct == null) {
            return;
        }
        if (ct.contains(ContentType.MULTIPART.v()) || ct.contains(ContentType.FORM_URLENCODED.v())) {
            return;
        }
        context.builder().header(Header.CONTENT_TYPE.v(), ct + ";charset=" + context.spec().charset().name());
    }
}


