package com.eventor.internal.meta;

public class MetaHandler {
    public final Class<?> expected;
    public final String dispatchField;
    public final boolean alwaysFinish;
    public final boolean alwaysStart;


    public MetaHandler(Class<?> expected, String dispatchField, boolean alwaysFinish, boolean alwaysStart) {
        this.expected = expected;
        this.dispatchField = dispatchField;
        this.alwaysFinish = alwaysFinish;
        this.alwaysStart = alwaysStart;
    }
}
