package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class UrlTest {

    @Test
    void test1() throws Exception {
        Res res = Req.get(TestHttpServer.url("/get/one/two")).ok();

        Assertions.assertEquals("ok", res.str());
    }

    @Test
    void test2() {
        URI base = URI.create(TestHttpServer.url("/"));
        Res res = Req.of()
                .scheme(base.getScheme())
                .host(base.getHost())
                .port(base.getPort())
                .path("get")
                .path("one")
                .path("two")
                .ok();

        Assertions.assertEquals("ok", res.str());
    }

    @Test
    void test3() throws Exception {
        URI base = URI.create(TestHttpServer.url("/"));
        Res res = Req.get("/get")
                .scheme(base.getScheme())
                .host(base.getHost())
                .port(base.getPort())
                .path("one")
                .path("two")
                .ok();

        Assertions.assertEquals("ok", res.str());
    }
}
