package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.OK;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.net.Proxy.Type;

public class ProxyTest {

    @Test
    void test1() throws Exception {

        OK.conf()
                .proxy("127.0.0.1", 80)
                .proxy(Type.SOCKS, "127.0.0.1", 80)
                .proxyAuthenticator("k", "pass");

        final Res res = Req.get("http://localhost:8080/get/one/two")
                .query("a", "1")
                .ok();
    }

    @Test
    void test2() throws Exception {

        final Res res = Req.get("http://localhost:8080/get/one/two")
                .config(e -> e.proxy("127.0.0.1", 80)
                        .proxy(Type.SOCKS, "127.0.0.1", 80)
                        .proxyAuthenticator("k", "pass"))
                .query("a", "1")
                .ok();
    }

}
