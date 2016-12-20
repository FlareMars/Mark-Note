package com.flaremars.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by FlareMars on 2016/10/21.
 */

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.CLASS)
@Inherited
public @interface Column {
    boolean required() default true;
    boolean updated() default true;
    boolean findBy() default false;
}
