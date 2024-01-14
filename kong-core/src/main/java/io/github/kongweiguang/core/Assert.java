package io.github.kongweiguang.core;

public class Assert {

    public static void notNull(final Object obj, final String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void isTure(final boolean bool, final String msg) {
        if (!bool) {
            throw new IllegalArgumentException(msg);
        }
    }

}
