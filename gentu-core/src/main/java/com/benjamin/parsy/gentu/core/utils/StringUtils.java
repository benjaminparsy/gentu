package com.benjamin.parsy.gentu.core.utils;

/**
 * Utility methods for string checks used during annotation filtering.
 */
public class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("StringUtils is a utility class and cannot be instantiated");
    }

    /**
     * Returns {@code true} if {@code value} is non-null and contains at least one non-whitespace character.
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * Returns {@code true} if {@code value} is {@code null}, empty, or contains only whitespace.
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isBlank();
    }

}
