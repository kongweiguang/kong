package io.github.kongweiguang.http.client.retry;

import java.util.concurrent.Callable;

public interface RetryPolicy<R> {


    /**
     * 处理 execute 数据
     */
    R execute(Callable<R> task) throws Exception;
}

