package com.eventor.internal.meta;

import com.eventor.internal.reflection.EventorReflections;

import java.lang.reflect.Method;
import java.util.Collection;

import static com.eventor.internal.reflection.EventorReflections.invoke;

public class MetaHandler {
    public final Method target;
    public final Class<?> expected;
    public final String dispatchField;
    public final boolean alwaysFinish;
    public final boolean alwaysStart;
    public final String idField;


    public MetaHandler(Method target, Class<?> expected, String dispatchField, boolean alwaysFinish, boolean alwaysStart, String idField) {
        this.target = target;
        this.expected = expected;
        this.dispatchField = dispatchField;
        this.alwaysFinish = alwaysFinish;
        this.alwaysStart = alwaysStart;
        this.idField = idField;
    }

    public Collection<?> execute(Object obj, Object arg) {
        return invoke(obj, target, arg);
    }

    public Object extractId(Object obj) {
        return EventorReflections.retrieveNamedValue(obj, idField);
    }
}
