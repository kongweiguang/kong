package io.github.kongweiguang.core;

public class If {

    public static void trueRun(boolean bool, Runnable r) {
        if (bool) {
            r.run();
        }
    }
}
