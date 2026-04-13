package io.github.kongweiguang.http.client.body;

import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static java.util.Objects.isNull;

/**
 * 按请求规格中的 contentType 编码原始字节。
 *
 * @author kongweiguang
 */
public final class RawBodyEncoder implements BodyEncoder {

    /**
     * 按请求内容类型编码原始字节请求体。
     */
    @Override
    public RequestBody encode(HttpReqSpec spec) {
        if (isNull(spec.body())) {
            return null;
        }

        return RequestBody.create(MediaType.parse(spec.contentType()), spec.body());
    }
}

