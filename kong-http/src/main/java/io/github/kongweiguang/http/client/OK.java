package io.github.kongweiguang.http.client;

import io.github.kongweiguang.core.util.Threads;
import io.github.kongweiguang.http.client.core.Conf;
import io.github.kongweiguang.http.client.core.ReqTypeEnum;
import io.github.kongweiguang.http.client.sse.SSEListener;
import io.github.kongweiguang.http.client.ws.WSListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.sse.EventSources;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.If.trueRun;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * 发送请求
 *
 * @author kongweiguang
 */
public final class OK {
    private final OkHttpClient client;
    private final ReqBuilder reqBuilder;
    private boolean async;
    private final boolean retry;

    private OK(final ReqBuilder reqBuilder, final OkHttpClient client, final boolean async) {
        this.client = client;
        this.reqBuilder = reqBuilder;
        this.async = async;
        this.retry = reqBuilder.max() > 0;
    }

    /**
     * <h2>发送请求</h2>
     * <p>
     * 只有http请求有返回值，ws和sse没有返回值
     *
     * @param reqBuilder 请求参数 {@link Req}
     * @param client     OkHttpClient {@link OkHttpClient}
     * @return Res {@link Res}
     */
    public static Res ok(final ReqBuilder reqBuilder, final OkHttpClient client) {
        return new OK(reqBuilder, client, false).ojbk().join();
    }

    /**
     * <h2>异步调用发送请求</h2>
     * 只有http请求有返回值，ws和sse没有返回值
     *
     * @param reqBuilder 请求参数 {@link Req}
     * @param client     OkHttpClient {@link OkHttpClient}
     * @return Res {@link Res}
     */
    public static CompletableFuture<Res> okAsync(final ReqBuilder reqBuilder, final OkHttpClient client) {
        return new OK(reqBuilder, client, true).ojbk();
    }

    /**
     * 实际发送请求
     *
     * @return 结果
     */
    private CompletableFuture<Res> ojbk() {

        //请求类型判断
        switch (reqType()) {
            case http:
                return http0(max());
            case ws:
                ws0();
                break;
            case sse:
                sse0();
                break;
        }

        return completedFuture(null);
    }


    /**
     * http请求
     *
     * @param max 重试次数
     * @return 响应结果
     */
    private CompletableFuture<Res> http0(final AtomicInteger max) {

        if (async()) {
            return supplyAsync(this::execute, exec())
                    .handle((r, t) -> asyncHandle(max, r, t));
        } else {
            return completedFuture(execute())
                    .handle((r, t) -> syncHandle(max, r, t));
        }

    }

    /**
     * 处理同步请求
     *
     * @param max       重试次数
     * @param r         结果
     * @param throwable 异常
     * @return 结果对象
     */
    private Res syncHandle(final AtomicInteger max, final Res r, final Throwable throwable) {
        if (handleRetry(max, r, throwable)) {
            return http0(max).join();
        }

        return r;
    }

    /**
     * 处理异步同步请求
     *
     * @param max       重试次数
     * @param r         结果
     * @param throwable 异常
     * @return 结果对象
     */
    private Res asyncHandle(final AtomicInteger max, final Res r, final Throwable throwable) {
        if (handleRetry(max, r, throwable)) {
            this.async = false;

            return http0(max).join();
        }

        ofNullable(fail())
                .ifPresent(fn -> trueRun(nonNull(throwable) || !r.isOk(), () -> fn.accept(throwable)));

        ofNullable(success())
                .ifPresent(fn -> trueRun(r.isOk(), () -> fn.accept(r)));

        return r;
    }


    /**
     * 处理是否重试
     *
     * @param max 重试次数
     * @param r   响应结果
     * @param t   异常
     * @return 是否重试
     */
    private boolean handleRetry(final AtomicInteger max, final Res r, final Throwable t) {
        if (retry() && (max.getAndDecrement() > 0 && predicate().test(r, t))) {
            Threads.sleep(delay().toMillis());
            return true;
        }

        return false;
    }

    /**
     * 提交请求
     *
     * @return 响应结果 {@link Res}
     */
    private Res execute() {
        try {
            return Res.of(client().newCall(request()).execute());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ws请求
     */
    private void ws0() {
        client().newWebSocket(request(), wsListener());
    }

    /**
     * sse请求
     */
    private void sse0() {
        EventSources.createFactory(client()).newEventSource(request(), sseListener());
    }

    //config
    public static Conf conf() {
        return Conf.global();
    }

    //get
    private OkHttpClient client() {
        return client;
    }

    private Request request() {
        return reqBuilder.builder().build();
    }

    private ReqBuilder reqBuilder() {
        return reqBuilder;
    }

    private boolean async() {
        return async;
    }

    private boolean retry() {
        return retry;
    }

    private Executor exec() {
        return reqBuilder.config().exec();
    }

    private BiPredicate<Res, Throwable> predicate() {
        return reqBuilder().predicate();
    }

    private Duration delay() {
        return reqBuilder().delay();
    }

    private Consumer<Res> success() {
        return reqBuilder().success();
    }

    private Consumer<Throwable> fail() {
        return reqBuilder().fail();
    }

    private WSListener wsListener() {
        return reqBuilder().wsListener();
    }

    private SSEListener sseListener() {
        return reqBuilder().sseListener();
    }

    private AtomicInteger max() {
        return new AtomicInteger(reqBuilder().max());
    }

    private ReqTypeEnum reqType() {
        return reqBuilder().reqType();
    }
}
