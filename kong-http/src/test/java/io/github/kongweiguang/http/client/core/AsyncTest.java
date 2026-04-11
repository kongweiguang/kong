package io.github.kongweiguang.http.client.core;


import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AsyncTest {

    @Test
    public void test1() throws Exception {
        AtomicReference<String> successBody = new AtomicReference<>();
        CompletableFuture<Res> future = Req.get(TestHttpServer.url("/get"))
                .query("a", "1")
                .success(r -> successBody.set(r.str()))
                .okAsync();

        Res res = future.get(30, TimeUnit.SECONDS);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(200, res.code());
        Assertions.assertEquals("ok", successBody.get());
    }

    @Test
    public void test2() throws Exception {
        AtomicBoolean failCalled = new AtomicBoolean(false);
        CompletableFuture<Res> future = Req.get("http://127.0.0.1:1/error")
                .fail(t -> failCalled.set(true))
                .okAsync();

        Res res = future.get(30, TimeUnit.SECONDS);
        Assertions.assertNull(res);
        Assertions.assertTrue(failCalled.get());
    }
}
