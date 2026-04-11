package io.github.kongweiguang.http.client.pipeline;

import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Header;

public final class ContentTypeStep implements RequestBuildStep {

    /**
     * 执行 apply 操作
     */
    @Override
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

