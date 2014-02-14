package com.eventor.api;


public @interface EventHandler {
    String idField() default "";
}
