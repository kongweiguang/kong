package io.github.kongweiguang.http.client.v2.body;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.RequestBody;

/**
 * 根据请求规格编码请求体。
 */
public interface BodyEncoder {
    RequestBody encode(HttpRequestSpec spec);
}


