package io.github.kongweiguang.http.client.pipeline.step;

import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Header;
import io.github.kongweiguang.http.client.pipeline.RequestBuildContext;
import io.github.kongweiguang.http.client.spec.ReqSpec;

/**
 * 请求头 Content-Type 处理步骤。
 *
 * @author kongweiguang
 */
public final class ContentTypeStep<S extends ReqSpec<?, ?>> implements RequestBuildStep<S> {

    /**
     * 执行 apply 操作
     */
    @Override
    public void apply(RequestBuildContext<S> context) {
        String ct = context.spec().contentType();
        if (ct == null) {
            return;
        }

        if (ct.contains(ContentType.MULTIPART.value()) || ct.contains(ContentType.FORM_URLENCODED.value())) {
            return;
        }

        context.spec().builder().header(Header.CONTENT_TYPE.value(), ct + ";charset=" + context.spec().charset().name());
    }
}

