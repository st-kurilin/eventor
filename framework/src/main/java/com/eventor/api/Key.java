package com.eventor.api;

public @interface Key {
    String field() default "";
    Class<?>[] classes() default Key.class;
}
