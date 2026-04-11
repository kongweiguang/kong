package io.github.kongweiguang.http.client;

/**
 * 处理异步执行的成功与失败回调
 *
 * @author kongweiguang
 */
public interface ResultHandler<R> {

    /**
     * 执行 on success 操作
     */
    void onSuccess(R result);

    /**
     * 执行 on failure 操作
     */
    void onFailure(Throwable error);


    /**
     * 返回 noop
     */
    static <R> ResultHandler<R> noop() {
        return new ResultHandler<>() {
            /**
             * noop 成功处理。
             */
            @Override
            public void onSuccess(R result) {
            }

            /**
             * noop 失败处理。
             */
            @Override
            public void onFailure(Throwable error) {
            }
        };
    }
}

