package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.TestHttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class DowTest {

    @Test
    void testDow() throws IOException {
        Res ok = Req.get(TestHttpServer.url("/download")).ok();

        Path file = Path.of(System.getProperty("java.io.tmpdir"), "kong-http-dow-" + UUID.randomUUID() + ".txt");
        ok.file(file.toString());
        Assertions.assertEquals("download-content", Files.readString(file));
    }
}
