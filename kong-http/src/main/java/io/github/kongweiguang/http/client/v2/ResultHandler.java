package io.github.kongweiguang.http.client.v2;

/**
 * Handles success and failure events for asynchronous execution.
 */
public interface ResultHandler<R> {
    void onSuccess(R result);

    void onFailure(Throwable error);

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

