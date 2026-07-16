package com.benjamin.parsy.gentu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * References an input file associated with the {@link Given} context of a test case.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface File {

    /**
     * Path to the file — either a classpath resource name or an absolute filesystem path,
     * depending on {@link #inClasspath()}.
     */
    String path();

    /**
     * {@code true} if {@link #path()} is a classpath resource; {@code false} for an absolute path.
     */
    boolean inClasspath() default false;

}
