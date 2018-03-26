package com.d.lib.xmlparser.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ElementMap {
    String name() default "";

    String entry() default "";

    String value() default "";

    String key() default "";

    Class keyType() default void.class;

    Class valueType() default void.class;

    boolean attribute() default false;

    boolean required() default true;

    boolean data() default false;

    boolean inline() default false;

    boolean empty() default true;
}
