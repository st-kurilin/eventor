package com.eventor.api.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface Finish {
    public static final String RESULT = "FinishFlag";
}
