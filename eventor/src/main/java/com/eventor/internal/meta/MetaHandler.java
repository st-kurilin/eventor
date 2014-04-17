package com.eventor.internal.meta;

import com.eventor.internal.EventorReflections;

import java.lang.reflect.Method;
import java.util.Collection;

import static com.eventor.internal.EventorReflections.invoke;

public class MetaHandler {
    public final Method target;
    @Deprecated //use match()
    public final Class<?> expected;
    public final String dispatchField;
    public final boolean alwaysFinish;
    public final boolean alwaysStart;
    public final String idField;
    public final boolean replyable;


    public MetaHandler(Method target, Class<?> expected, String dispatchField, boolean alwaysFinish, boolean alwaysStart, String idField, boolean replyable) {
        this.target = target;
        this.expected = expected;
        this.dispatchField = dispatchField;
        this.alwaysFinish = alwaysFinish;
        this.alwaysStart = alwaysStart;
        this.idField = idField;
        this.replyable = replyable;
    }

    public Collection<?> execute(Object obj, Object arg) {
        return invoke(obj, target, arg);
    }

    public Object extractId(Object obj) {
        if (idField == null) return null;
        return EventorReflections.retrieveNamedValue(obj, idField);
    }

    public boolean match(Class<?> actual) {
        return expected.isAssignableFrom(actual);
    }
}
