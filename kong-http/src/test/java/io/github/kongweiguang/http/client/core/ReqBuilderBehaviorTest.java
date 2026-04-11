package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.builder.DefHTTPReqBuilder;
import io.github.kongweiguang.http.common.core.Header;
import io.github.kongweiguang.http.common.core.Method;
import okhttp3.FormBody;
import okhttp3.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class ReqBuilderBehaviorTest {

    @Test
    void retryShouldRejectNullConsumer() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Req.of().retry(null)
        );

        Assertions.assertEquals("retry consumer must not be null", ex.getMessage());
    }

    @Test
    void formUrlencodedShouldEncodeRawValue() {
        ExposedBuilder builder = new ExposedBuilder();
        builder.method(Method.POST)
                .url("http://localhost:8080/post")
                .contentType("application/x-www-form-urlencoded")
                .form("q", "a+b");

        builder.prepare();
        Request request = builder.request();

        Assertions.assertInstanceOf(FormBody.class, request.body());
        FormBody formBody = (FormBody) request.body();
        Assertions.assertEquals("q", formBody.encodedName(0));
        Assertions.assertEquals("a%2Bb", formBody.encodedValue(0));
    }

    @Test
    void emptyCookiesShouldNotEmitCookieHeader() {
        ExposedBuilder builder = new ExposedBuilder();
        builder.url("http://localhost:8080/get")
                .cookies(new HashMap<>());

        builder.prepare();
        Request request = builder.request();

        Assertions.assertNull(request.header(Header.COOKIE.v()));
    }

    @Test
    void invalidUrlShouldThrow() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Req.of().url("::invalid::")
        );

        Assertions.assertTrue(ex.getMessage().contains("invalid url"));
    }

    @Test
    void reqFactoryShouldApplyMethodAndUrl() {
        DefHTTPReqBuilder builder = Req.put("http://localhost:8080/path");

        Assertions.assertEquals(Method.PUT, builder.method());
        Assertions.assertEquals("http://localhost:8080/path", builder.urlBuilder().build().toString());
    }

    static class ExposedBuilder extends DefHTTPReqBuilder {
        void prepare() {
            before();
        }

        Request request() {
            return builder().build();
        }
    }
}
