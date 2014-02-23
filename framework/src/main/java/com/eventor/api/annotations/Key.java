package com.eventor.api.annotations;

public @interface Key {
    String field() default "";

    Class<?>[] classes() default Key.class;
}
