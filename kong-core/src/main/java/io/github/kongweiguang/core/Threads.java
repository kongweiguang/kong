package io.github.kongweiguang.core;

public class Threads {
    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {

        }
    }


    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static void sync(final Object obj) {
        synchronized (obj) {
            try {
                obj.wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

}
