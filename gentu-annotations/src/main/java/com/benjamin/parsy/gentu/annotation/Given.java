package com.benjamin.parsy.gentu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes the input data and preconditions of a test case.
 *
 * <p>Empty strings in {@link #value()} are filtered out during report generation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Given {

    /**
     * Textual descriptions of the input data or preconditions. Empty values are ignored.
     */
    String[] value() default "";

    /**
     * Files used as input data for the test case.
     */
    File[] files() default {};

}
