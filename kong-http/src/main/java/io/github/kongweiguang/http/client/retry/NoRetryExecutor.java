package io.github.kongweiguang.http.client.retry;

import java.util.concurrent.Callable;

/**
 * 只执行一次，不进行重试。
 *
 * @author kongweiguang
 */
public final class NoRetryExecutor<R> implements RequestRetryExecutor<R> {

    /**
     * 直接执行任务，不引入额外重试逻辑。
     */
    @Override
    public R execute(Callable<R> task) throws Exception {
        return task.call();
    }
}

