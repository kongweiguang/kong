package io.github.kongweiguang.core;

import static java.util.Objects.isNull;

public class Str {

    public static boolean isEmpty(final String str) {
        if (isNull(str)) {
            return true;
        }

        return str.isEmpty();
    }

    public static String defaultIfEmpty(final String str, final String d) {
        if (isEmpty(str)) {
            return d;
        }

        return str;
    }

}
