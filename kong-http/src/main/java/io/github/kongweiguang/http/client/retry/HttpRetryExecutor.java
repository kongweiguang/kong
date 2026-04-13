package io.github.kongweiguang.http.client.retry;

import io.github.kongweiguang.core.lang.Assert;
import io.github.kongweiguang.core.retry.IntervalStrategy;
import io.github.kongweiguang.core.retry.RetryContext;
import io.github.kongweiguang.core.retry.RetryExecutor;
import io.github.kongweiguang.core.retry.RetryPolicy;
import io.github.kongweiguang.http.client.Res;

import java.util.concurrent.Callable;

/**
 * 将 HTTP 重试配置转换为 core retry 执行语义。
 *
 * @author kongweiguang
 */
public final class HttpRetryExecutor implements RequestRetryExecutor<Res> {

    private final RetryPolicy<Res> policy;

    /**
     * 根据 HTTP 配置创建实际执行策略。
     */
    public HttpRetryExecutor(HttpRetryConfig config) {
        Assert.notNull(config, "config parameter cannot be null");
        this.policy = toPolicy(config);
    }

    /**
     * HTTP 侧保持“成功返回响应，最终失败抛异常”的调用语义。
     */
    @Override
    public Res execute(Callable<Res> task) throws Exception {
        return RetryExecutor.execute(policy, task::call);
    }

    private RetryPolicy<Res> toPolicy(HttpRetryConfig config) {
        return RetryPolicy.<Res>builder()
                .maxAttempts(config.maxAttempts())
                .interval(IntervalStrategy.fixed(config.delay()))
                .retryIf(context -> shouldRetry(config, context))
                .build();
    }

    private boolean shouldRetry(HttpRetryConfig config, RetryContext<Res> context) {
        if (context.error() != null) {
            return config.exceptionPredicate().test(context.error());
        }
        return config.responsePredicate().test(context.value());
    }
}

