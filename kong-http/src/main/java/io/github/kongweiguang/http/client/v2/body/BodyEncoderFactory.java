package io.github.kongweiguang.http.client.v2.body;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import io.github.kongweiguang.http.common.core.ContentType;

/**
 * Chooses the body encoder by content type.
 */
public final class BodyEncoderFactory {
    private static final BodyEncoder RAW = new RawBodyEncoder();
    private static final BodyEncoder FORM = new FormUrlEncodedEncoder();
    private static final BodyEncoder MULTIPART = new MultipartEncoder();

    private BodyEncoderFactory() {
    }

    public static BodyEncoder resolve(HttpRequestSpec spec) {
        String ct = spec.contentType();
        if (ct != null && ct.contains(ContentType.MULTIPART.v())) {
            return MULTIPART;
        }
        if (ct != null && ct.contains(ContentType.FORM_URLENCODED.v())) {
            return FORM;
        }
        return RAW;
    }
}

