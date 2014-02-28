package com.eventor.internal;

public class EventorPreconditions {
    public static void assume(boolean condition, String str, Object... arg) {
        if (!condition) {
            throw new IllegalStateException(String.format(str, arg));
        }
    }
}
