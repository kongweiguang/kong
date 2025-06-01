package io.github.kongweiguang.http.client.ok;

import io.github.kongweiguang.core.lang.Opt;
import io.github.kongweiguang.core.retry.RetryableTask;
import io.github.kongweiguang.http.client.OK;
import io.github.kongweiguang.http.client.Res;
import io.github.kongweiguang.http.client.builder.HttpReqBuilder;
import io.github.kongweiguang.http.common.exception.KongHttpRuntimeException;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Opt.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * HTTP请求发送器
 *
 * @author kongweiguang
 */
public class HttpOK extends OK<HttpReqBuilder<?, Res>, Res> {
    private final RetryableTask<Res> retryTask;

    private HttpOK(HttpReqBuilder<?, Res> reqBuilder, OkHttpClient client) {
        super(reqBuilder, client);
        this.retryTask = reqBuilder.retry();
    }

    /**
     * <h2>异步调用发送HTTP请求</h2>
     *
     * @param reqBuilder 请求参数 {@link HttpReqBuilder}
     * @param client     OkHttpClient {@link OkHttpClient}
     * @return CompletableFuture<Res> 异步响应结果
     */
    public static CompletableFuture<Res> ok(HttpReqBuilder<?, Res> reqBuilder, OkHttpClient client) {
        return new HttpOK(reqBuilder, client).ojbk();
    }

    /**
     * 实际发送请求
     *
     * @return 结果
     */
    private CompletableFuture<Res> ojbk() {
        return supplyAsync(
                () -> {
                    Opt<Res, Throwable> resOpt = retryTask.task(this::execute)
                            .execute()
                            .get();

                    Opt<Consumer<Res>, Object> successOpt = ofNullable(success());
                    Opt<Consumer<Throwable>, Object> failOpt = ofNullable(fail());

                    // 异步异常使用回调
                    if (successOpt.isPresent() || failOpt.isPresent()) {
                        resOpt.match(
                                r -> successOpt.ifPresent(fn -> fn.accept(r)),
                                () -> {
                                },
                                t -> failOpt.ifPresent(fn -> fn.accept(t))

                        );
                    }
                    // 同步异常直接抛
                    else {

                        if (resOpt.isError()) {
                            throw new KongHttpRuntimeException(resOpt.getError());
                        }

                    }

                    return resOpt.value();
                },
                reqBuilder().config().exec());
    }


    /**
     * 执行HTTP请求
     *
     * @return 响应结果
     */
    @Override
    protected Res execute() {
        try {
            return Res.of(client().newCall(request()).execute());
        } catch (IOException e) {
            throw new KongHttpRuntimeException(e);
        }
    }

    private Consumer<Res> success() {
        return reqBuilder().success();
    }

    private Consumer<Throwable> fail() {
        return reqBuilder().fail();
    }

}