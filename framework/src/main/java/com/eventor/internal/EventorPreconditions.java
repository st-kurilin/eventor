package com.eventor.internal;

public class EventorPreconditions {
    public static void assume(boolean condition, String str, Object... arg) {
        if (!condition) {
            throw new IllegalStateException(String.format(str, arg));
        }
    }

    public static <T> T assumeNotNull(T obj, String str, Object... arg) {
        if (obj == null) {
            throw new IllegalStateException(String.format(str, arg));
        }
        return obj;
    }
}
