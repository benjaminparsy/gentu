package com.benjamin.parsy.gentu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test method with structured metadata used to generate a human-readable Gentu report.
 *
 * <p>Place this annotation on JUnit test methods alongside {@code @Test}. The annotated method
 * must pass for the result to be included in the report.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestDescriptor {

    /**
     * Short human-readable name for the test case, used as the report entry title.
     */
    String testName();

    /**
     * Free-text description of what the test verifies.
     */
    Description description();

    /**
     * Input data and preconditions for the test case.
     */
    Given given();

    /**
     * Expected outcomes of the test case.
     */
    Expected expected();

}
