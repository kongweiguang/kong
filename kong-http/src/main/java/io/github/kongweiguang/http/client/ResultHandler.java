package io.github.kongweiguang.http.client;

/**
 * 处理异步执行的成功与失败回调。
 */
public interface ResultHandler<R> {
    void onSuccess(R result);

    void onFailure(Throwable error);

    static <R> ResultHandler<R> noop() {
        return new ResultHandler<>() {
            @Override
            /**
             * 处理执行成功后的回调。
             */
            public void onSuccess(R result) {
            }

            @Override
            /**
             * 处理执行失败后的回调。
             */
            public void onFailure(Throwable error) {
            }
        };
    }
}


