package io.github.kongweiguang.http.client.body;

import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 编码 form-url-encoded 表单数据。
 *
 * @author kongweiguang
 */
public final class FormUrlEncodedEncoder implements BodyEncoder {

    /**
     * 将表单字段编码为 `application/x-www-form-urlencoded` 请求体。
     */
    @Override
    public RequestBody encode(HttpReqSpec spec) {
        FormBody.Builder fb = new FormBody.Builder(spec.charset());
        spec.form().forEach(fb::add);
        return fb.build();
    }
}

