package com.d.lib.xmlparser.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Element {
    String name() default "";

    boolean data() default false;

    boolean required() default true;

    Class type() default void.class;
}
