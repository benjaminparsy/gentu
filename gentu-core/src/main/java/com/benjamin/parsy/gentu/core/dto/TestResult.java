package com.benjamin.parsy.gentu.core.dto;

import java.nio.file.Path;
import java.util.List;

/**
 * Immutable representation of a single test result, combining {@code @TestDescriptor} metadata
 * with execution context (class, method, timestamp).
 *
 * @param id                sequential identifier assigned during report generation
 * @param testName          human-readable name from {@code @TestDescriptor}
 * @param description       free-text description from {@code @Description}
 * @param givenValue        non-blank textual preconditions from {@code @Given}
 * @param absoluteFilePaths file-based inputs from {@code @Given}
 * @param expected          non-blank expected outcomes from {@code @Expected}
 * @param className         simple name of the test class
 * @param methodName        name of the test method
 */
public record TestResult(
        int id,
        String testName,
        String description,
        List<String> givenValue,
        List<Path> absoluteFilePaths,
        List<String> expected,
        String className,
        String methodName
) {

}
