package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.body.BodyEncoderFactory;
import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Method;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BodyEncoderFactoryTest {

    @Test
    void shouldBuildFormBody() {
        HttpRequestSpec spec = Req.post("http://localhost/form")
                .contentType(ContentType.FORM_URLENCODED.v())
                .form("k", "v")
                ;

        RequestBody body = BodyEncoderFactory.resolve(spec).encode(spec);

        Assertions.assertInstanceOf(FormBody.class, body);
    }

    @Test
    void shouldBuildMultipartBody() {
        HttpRequestSpec spec = Req.post("http://localhost/multipart")
                .contentType(ContentType.MULTIPART.v())
                .form("name", "kong")
                .file("f", "a.txt", "abc".getBytes())
                ;

        RequestBody body = BodyEncoderFactory.resolve(spec).encode(spec);

        Assertions.assertInstanceOf(MultipartBody.class, body);
    }

    @Test
    void shouldBuildRawBody() {
        HttpRequestSpec spec = Req.of("http://localhost/raw")
                .method(Method.POST)
                .contentType(ContentType.JSON.v())
                .body("{\"a\":1}")
                ;

        RequestBody body = BodyEncoderFactory.resolve(spec).encode(spec);

        Assertions.assertNotNull(body);
    }
}
