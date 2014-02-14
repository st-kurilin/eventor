package com.eventor.api;

public @interface Aggregate {
    Class<?> initBy();
}
