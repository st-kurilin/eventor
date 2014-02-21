package com.eventor.api;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Aggregate {
    Class<?> initBy() default Aggregate.class;
}
