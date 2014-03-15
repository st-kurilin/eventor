package com.eventor.api.annotations;

@Deprecated //It seems we don't need it
public @interface Key {
    String field() default "";

    Class<?>[] classes() default Key.class;
}
