package com.eventor.internal.meta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MetaHandler {
    public final Class<?> expected;
    public final String dispatchField;
    public final boolean alwaysFinish;
    public final boolean alwaysStart;
    public final String idField;


    public MetaHandler(Class<?> expected, String dispatchField, boolean alwaysFinish, boolean alwaysStart, String idField) {
        this.expected = expected;
        this.dispatchField = dispatchField;
        this.alwaysFinish = alwaysFinish;
        this.alwaysStart = alwaysStart;
        this.idField = idField;
    }

    public Object execute(Object target, Object arg) {
        try {
            for (Method m : target.getClass().getMethods()) {
                if (m.getParameterTypes().length == 1) {
                    if (m.getParameterTypes()[0] == expected) {
                        return m.invoke(target, arg);
                    }
                }
            }
            throw new IllegalStateException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Object extractId(Object cmd) {
        try {
            for (Field each : cmd.getClass().getDeclaredFields()) {
                if (each.getName().equals(idField)) {
                    each.setAccessible(true);
                    return each.get(cmd);
                }
            }
            throw new RuntimeException(String.format("Could not find %s in %s", idField, cmd));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
