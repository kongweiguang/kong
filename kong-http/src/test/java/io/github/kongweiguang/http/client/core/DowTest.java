package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class DowTest {

    @Test
    void testDow() {
        final Res ok = Req.get("http://localhost:80/get_file").ok();

        try {
            ok.file("d:\\k.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
