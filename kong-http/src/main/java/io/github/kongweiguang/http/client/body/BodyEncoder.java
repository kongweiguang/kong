package io.github.kongweiguang.http.client.body;

import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import okhttp3.RequestBody;

/**
 * 根据请求规格编码请求体
 *
 * @author kongweiguang
 */
public interface BodyEncoder {
    RequestBody encode(HttpReqSpec spec);
}

