package io.github.kongweiguang.http.client.v2.body;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static java.util.Objects.isNull;

/**
 * Encodes raw bytes using content type from request spec.
 */
public final class RawBodyEncoder implements BodyEncoder {
    @Override
    public RequestBody encode(HttpRequestSpec spec) {
        if (isNull(spec.body())) {
            return null;
        }
        return RequestBody.create(MediaType.parse(spec.contentType()), spec.body());
    }
}
