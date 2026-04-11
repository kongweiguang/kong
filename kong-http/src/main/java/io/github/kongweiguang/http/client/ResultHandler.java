package io.github.kongweiguang.http.client;

/**
 * 处理异步执行的成功与失败回调
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
            @Override

            public void onSuccess(R result) {
            }

            @Override

            public void onFailure(Throwable error) {
            }
        };
    }
}

