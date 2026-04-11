package io.github.kongweiguang.http.client.utils;

import io.github.kongweiguang.http.client.utils.HttpClientUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpClientUtilTest {

    @Test
    void shouldReturnDefaultWhenUrlIsNullOrBlank() {
        Assertions.assertEquals("http://localhost", HttpClientUtil.fixUrl(null));
        Assertions.assertEquals("http://localhost", HttpClientUtil.fixUrl("   "));
    }

    @Test
    void shouldKeepHttpAndHttpsUrl() {
        Assertions.assertEquals("http://example.com", HttpClientUtil.fixUrl("http://example.com"));
        Assertions.assertEquals("https://example.com", HttpClientUtil.fixUrl(" HTTPS://example.com "));
    }

    @Test
    void shouldConvertWsAndWssToHttpAndHttps() {
        Assertions.assertEquals("http://example.com/ws", HttpClientUtil.fixUrl("ws://example.com/ws"));
        Assertions.assertEquals("https://example.com/ws", HttpClientUtil.fixUrl("WSS://example.com/ws"));
    }

    @Test
    void shouldRepairUrlWithoutScheme() {
        Assertions.assertEquals("http://example.com", HttpClientUtil.fixUrl("example.com"));
        Assertions.assertEquals("http://localhost:8080/api", HttpClientUtil.fixUrl("localhost:8080/api"));
        Assertions.assertEquals("http://127.0.0.1:9000", HttpClientUtil.fixUrl("127.0.0.1:9000"));
    }

    @Test
    void shouldRepairPathLikeInput() {
        Assertions.assertEquals("http://localhost/api", HttpClientUtil.fixUrl("/api"));
        Assertions.assertEquals("http://localhost/api/v1", HttpClientUtil.fixUrl("api/v1"));
        Assertions.assertEquals("http://localhost/?a=1", HttpClientUtil.fixUrl("?a=1"));
    }

    @Test
    void shouldRepairSchemeRelativeUrl() {
        Assertions.assertEquals("http://example.com/path", HttpClientUtil.fixUrl("//example.com/path"));
    }
}
