package io.github.kongweiguang.http.client.v2.body;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Encodes urlencoded form data.
 */
public final class FormUrlEncodedEncoder implements BodyEncoder {
    @Override
    public RequestBody encode(HttpRequestSpec spec) {
        FormBody.Builder fb = new FormBody.Builder(spec.charset());
        spec.form().forEach(fb::add);
        return fb.build();
    }
}

