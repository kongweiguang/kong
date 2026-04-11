package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class DowTest {

    @Test
    void testDow() {
        Res ok = Req.get("http://localhost:8080/xz").ok();

        try {
            ok.file("C:\\test\\k.txt");
        } catch (IOException e) {
            throw new KongHttpRuntimeException(e);
        }
    }
}
