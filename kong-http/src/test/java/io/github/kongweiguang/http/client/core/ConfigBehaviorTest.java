package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.builder.DefHTTPReqBuilder;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Header;
import io.github.kongweiguang.http.common.core.Method;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.nio.charset.StandardCharsets;

class ConfigBehaviorTest {

    @Test
    void reqBuilderShouldUseIsolatedConf() {
        Assertions.assertNotSame(Conf.global(), Req.of().config());
    }

    @Test
    void confDefaultsShouldBeSecureAndRedirecting() {
        Conf conf = Conf.of();

        Assertions.assertTrue(conf.ssl());
        Assertions.assertTrue(conf.followRedirects());
        Assertions.assertTrue(conf.followSslRedirects());
    }

    @Test
    void clientShouldApplyProxySelector() {
        ProxySelector proxySelector = new ProxySelector() {
            @Override
            public List<java.net.Proxy> select(URI uri) {
                return List.of(java.net.Proxy.NO_PROXY);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            }
        };
        OkHttpClient client = Client.of(Conf.of().proxySelector(proxySelector));

        Assertions.assertSame(proxySelector, client.proxySelector());
    }

    @Test
    void contentTypeHeaderShouldUseLatestCharset() {
        ExposedBuilder builder = new ExposedBuilder();
        builder.method(Method.POST)
                .url("http://localhost:8080/post")
                .contentType(ContentType.JSON.v())
                .charset(StandardCharsets.UTF_16);

        builder.prepare();
        Request request = builder.request();

        Assertions.assertEquals(
                ContentType.JSON.v() + ";charset=" + StandardCharsets.UTF_16.name(),
                request.header(Header.CONTENT_TYPE.v())
        );
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
