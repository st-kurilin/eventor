package com.eventor.api.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Deprecated //USE @EventListener
public @interface EventHandler {
    String idField() default "";
}
