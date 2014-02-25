package com.eventor.api.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Saga {
    Class<?> initBy() default Saga.class;
}
