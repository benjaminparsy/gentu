package com.benjamin.parsy.gentu.core.utils;

import com.benjamin.parsy.gentu.core.ReportType;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Validation utilities for Gentu configuration parameters.
 * Each method throws {@link IllegalArgumentException} with a descriptive message on failure.
 */
public final class Assert {

    private Assert() {
        throw new UnsupportedOperationException("Assert is a utility class and cannot be instantiated");
    }

    /**
     * Asserts that {@code path} is an existing directory and is writable.
     *
     * @param path the path to validate
     * @throws IllegalArgumentException if {@code path} is not a directory or is not writable
     */
    public static void isWritableDirectory(Path path) {
        if (!FileUtils.isDirectory(path)) {
            throw new IllegalArgumentException("%s is not a directory".formatted(path));
        }
        if (!Files.isWritable(path)) {
            throw new IllegalArgumentException("directory %s is not writable".formatted(path));
        }
    }

    /**
     * Asserts that {@code reportType} matches a known {@link com.benjamin.parsy.gentu.core.ReportType} (case-insensitive).
     *
     * @param reportType the value to validate
     * @throws IllegalArgumentException if no matching report type exists
     */
    public static void isValidType(String reportType) {
        if (!ReportType.exists(reportType)) {
            throw new IllegalArgumentException("The %s report type does not exist".formatted(reportType));
        }
    }

}
