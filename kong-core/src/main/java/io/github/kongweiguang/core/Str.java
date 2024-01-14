package io.github.kongweiguang.core;

import static java.util.Objects.isNull;

public class Str {

    public static boolean isEmpty(final String str) {
        return isNull(str) || str.isEmpty();
    }

}
