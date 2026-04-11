package io.github.kongweiguang.http.client.core;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Proxy.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConfigTest {

    @Test
    void test1() {
        ExecutorService exec = Executors.newCachedThreadPool();
        ConnectionPool pool = new ConnectionPool(10, 10, TimeUnit.MINUTES);

        Conf conf = Conf.of()
                .proxy("127.0.0.1", 80)
                .proxy(Type.SOCKS, "127.0.0.1", 1080)
                .proxyAuthenticator("k", "pass")
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull final Chain chain) throws IOException {
                        return chain.proceed(chain.request());
                    }
                })
                .connectionPool(pool)
                .exec(exec);

        Assertions.assertNotNull(conf.proxy());
        Assertions.assertEquals(Type.SOCKS, conf.proxy().type());
        Assertions.assertNotNull(conf.proxyAuthenticator());
        Assertions.assertNotNull(conf.interceptors());
        Assertions.assertFalse(conf.interceptors().isEmpty());
        Assertions.assertSame(pool, conf.connectionPool());
        Assertions.assertSame(exec, conf.exec());
    }

}
