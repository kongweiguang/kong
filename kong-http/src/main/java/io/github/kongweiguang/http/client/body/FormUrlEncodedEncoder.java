package io.github.kongweiguang.http.client.body;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 编码 form-url-encoded 表单数据。
 */
public final class FormUrlEncodedEncoder implements BodyEncoder {
    @Override
    /**
     * 根据请求规格编码请求体。
     */
    public RequestBody encode(HttpRequestSpec spec) {
        FormBody.Builder fb = new FormBody.Builder(spec.charset());
        spec.form().forEach(fb::add);
        return fb.build();
    }
}


