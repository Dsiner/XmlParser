package com.d.lib.xmlparser.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ElementList {
    String name() default "";

    String entry() default "";

    Class type() default void.class;

    boolean data() default false;

    boolean required() default true;

    boolean inline() default false;

    boolean empty() default true;
}
