package io.github.kongweiguang.http.client.body;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static java.util.Objects.isNull;

/**
 * 按请求规格中的 contentType 编码原始字节。
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

