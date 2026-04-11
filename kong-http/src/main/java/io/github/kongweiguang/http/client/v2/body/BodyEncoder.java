package io.github.kongweiguang.http.client.v2.body;

import io.github.kongweiguang.http.client.HttpRequestSpec;
import okhttp3.RequestBody;

/**
 * Encodes request body from a request spec.
 */
public interface BodyEncoder {
    RequestBody encode(HttpRequestSpec spec);
}

