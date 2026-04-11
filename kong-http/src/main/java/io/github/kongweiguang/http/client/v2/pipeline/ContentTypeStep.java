package io.github.kongweiguang.http.client.v2.pipeline;

import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Header;

/**
 * Adds normalized content-type header for non-form/non-multipart payloads.
 */
public final class ContentTypeStep implements RequestBuildStep {
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

