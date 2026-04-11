package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.TestHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.Proxy.Type;

public class ProxyTest {

    @Test
    void test1() {
        Conf conf = Conf.of()
                .proxy("127.0.0.1", 80)
                .proxy(Type.SOCKS, "127.0.0.1", 1080)
                .proxyAuthenticator("k", "pass");

        Assertions.assertNotNull(conf.proxy());
        Assertions.assertNotNull(conf.proxyAuthenticator());
        Assertions.assertEquals(Type.SOCKS, conf.proxy().type());
    }

    @Test
    void test2() {
        var builder = Req.get(TestHttpServer.url("/get/one/two"))
                .config(e -> e.proxy("127.0.0.1", 80)
                        .proxy(Type.SOCKS, "127.0.0.1", 1080)
                        .proxyAuthenticator("k", "pass"))
                .query("a", "1");

        Assertions.assertNotNull(builder.config().proxy());
        Assertions.assertNotNull(builder.config().proxyAuthenticator());
        Assertions.assertEquals(Type.SOCKS, builder.config().proxy().type());
    }

}
