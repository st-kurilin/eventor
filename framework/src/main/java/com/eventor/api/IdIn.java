package com.eventor.api;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Inherited
public @interface IdIn {
    String value() default "id";
}
