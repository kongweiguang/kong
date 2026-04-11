package io.github.kongweiguang.http.client.retry;

import io.github.kongweiguang.core.retry.RetryableTask;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.exception.KongHttpRuntimeException;

import java.util.concurrent.Callable;

/**
 * 将 HTTP 重试行为委托给现有 RetryableTask 语义。
 */
public final class HttpRetryPolicy implements RetryPolicy<Res> {

    /**
     * 保存 retry task
     */
    private final RetryableTask<Res> retryTask;

    /**
     * 创建 HttpRetryPolicy instance
     */
    public HttpRetryPolicy(RetryableTask<Res> retryTask) {
        this.retryTask = retryTask;
    }

    /**
     * 处理 execute 数据
     */
    @Override
    public Res execute(Callable<Res> task) throws Exception {
        var result = retryTask.task(() -> {
                    try {
                        return task.call();
                    } catch (Exception e) {
                        throw new KongHttpRuntimeException(e);
                    }
                })
                .execute()
                .get();

        if (result.isError()) {
            Throwable error = result.getError();
            if (error instanceof Exception) {
                throw (Exception) error;
            }
            throw new KongHttpRuntimeException(error);
        }
        return result.value();
    }
}

