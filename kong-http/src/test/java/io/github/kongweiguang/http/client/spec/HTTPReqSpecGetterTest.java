package io.github.kongweiguang.http.client.spec;

import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.retry.HttpRetryConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HTTPReqSpecGetterTest {

    @Test
    void gettersShouldExposeCurrentState() {
        HttpReqSpec spec = new HttpReqSpec();
        Consumer<Res> success = res -> {
        };
        Consumer<Throwable> fail = err -> {
        };

        spec.success(success);
        spec.fail(fail);

        Map<String, String> form = spec.form();
        List<?> files = spec.files();
        HttpRetryConfig retry = spec.retry();

        assertNotNull(form);
        assertTrue(form.isEmpty());
        assertNotNull(files);
        assertTrue(files.isEmpty());
        assertNotNull(retry);
        assertSame(success, spec.success());
        assertSame(fail, spec.fail());
        assertSame(form, spec.form());
        assertSame(files, spec.files());
        assertSame(retry, spec.retry());
    }
}
