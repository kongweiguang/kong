package io.github.kongweiguang.http.client.executor;

import io.github.kongweiguang.http.client.retry.NoRetryExecutor;
import io.github.kongweiguang.http.client.spec.ReqSpec;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractExecutorTest {

    @Test
    void shouldUseVirtualThreadExecutorWhenConfExecIsNotConfigured() {
        TestSpec spec = new TestSpec();
        TestExecutor executor = new TestExecutor(spec);

        Thread thread = executor.executeAsync().join();

        assertTrue(thread.isVirtual());
    }

    @Test
    void shouldPreferConfiguredExecutorWhenConfExecIsConfigured() {
        TestSpec spec = new TestSpec();
        try (ExecutorService executorService = Executors.newSingleThreadExecutor(r -> new Thread(r, "custom-exec"))) {
            spec.config(conf -> conf.exec(executorService));
            TestExecutor executor = new TestExecutor(spec);

            Thread thread = executor.executeAsync().join();

            assertEquals("custom-exec", thread.getName());
        }
    }

    private static final class TestSpec extends ReqSpec<TestSpec, Thread> {

        @Override
        protected Thread execute(OkHttpClient client) {
            return Thread.currentThread();
        }
    }

    private static final class TestExecutor extends AbstractExecutor<TestSpec, Thread> {

        private TestExecutor(TestSpec spec) {
            super(spec, new OkHttpClient(), new NoRetryExecutor<>());
        }

        @Override
        protected Thread execute0() {
            return Thread.currentThread();
        }
    }
}
