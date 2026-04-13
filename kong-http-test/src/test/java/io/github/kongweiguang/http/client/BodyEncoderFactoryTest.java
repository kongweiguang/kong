package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.body.BodyEncoderFactory;
import io.github.kongweiguang.http.client.consts.ContentType;
import io.github.kongweiguang.http.client.consts.Method;
import io.github.kongweiguang.http.client.spec.HttpReqSpec;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BodyEncoderFactoryTest {

    @Test
    void shouldBuildFormBody() {
        HttpReqSpec spec = Req.post("http://localhost/form")
                .contentType(ContentType.FORM_URLENCODED.value())
                .form("k", "v");

        RequestBody body = BodyEncoderFactory.resolve(spec).encode(spec);

        Assertions.assertInstanceOf(FormBody.class, body);
    }

    @Test
    void shouldBuildMultipartBody() {
        HttpReqSpec spec = Req.post("http://localhost/multipart")
                .contentType(ContentType.MULTIPART.value())
                .form("name", "kong")
                .file("f", "a.txt", "abc".getBytes());

        RequestBody body = BodyEncoderFactory.resolve(spec).encode(spec);

        Assertions.assertInstanceOf(MultipartBody.class, body);
    }

    @Test
    void shouldBuildRawBody() {
        HttpReqSpec spec = Req.of("http://localhost/raw")
                .method(Method.POST)
                .contentType(ContentType.JSON.value())
                .json("{\"a\":1}");

        RequestBody body = BodyEncoderFactory.resolve(spec).encode(spec);

        Assertions.assertNotNull(body);
    }
}
