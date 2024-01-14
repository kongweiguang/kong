package io.github.kongweiguang.core;

import java.util.function.Supplier;

public class If {

    public static void trueRun(boolean bool, Runnable r) {
        if (bool) {
            r.run();
        }
    }

    public static <T> T trueOrSup(final boolean bool, Supplier<T> f1, Supplier<T> f2) {
        if (bool) {
            return f1.get();
        }

        return f2.get();
    }
}
