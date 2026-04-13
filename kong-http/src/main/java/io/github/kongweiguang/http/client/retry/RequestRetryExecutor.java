package io.github.kongweiguang.http.client.retry;

import java.util.concurrent.Callable;

/**
 * HTTP 请求重试执行器接口。
 *
 * @author kongweiguang
 */
public interface RequestRetryExecutor<R> {


    /**
     * 执行任务，并按约定的重试行为返回结果。
     */
    R execute(Callable<R> task) throws Exception;
}

