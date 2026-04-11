package io.github.kongweiguang.http.client;

import io.github.kongweiguang.http.client.core.Client;
import io.github.kongweiguang.http.client.core.Conf;
import io.github.kongweiguang.http.client.core.Timeout;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class ClientApplierTest {

    @Test
    void shouldApplyTimeoutAndRedirectFlags() {
        Conf conf = Conf.of()
                .followRedirects(false)
                .followSslRedirects(false)
                .timeout(new Timeout(Duration.ofSeconds(2), Duration.ofSeconds(3), Duration.ofSeconds(4)));

        OkHttpClient client = Client.of(conf);

        Assertions.assertFalse(client.followRedirects());
        Assertions.assertFalse(client.followSslRedirects());
        Assertions.assertEquals(2000, client.connectTimeoutMillis());
        Assertions.assertEquals(3000, client.writeTimeoutMillis());
        Assertions.assertEquals(4000, client.readTimeoutMillis());
    }
}
