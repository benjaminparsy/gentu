package com.benjamin.parsy.gentu.core;

import java.util.Arrays;
import java.util.Optional;

/**
 * Supported output formats for the Gentu test report.
 */
public enum ReportType {

    /**
     * Plain-text report ({@code .txt}).
     */
    TEXT;

    /**
     * Returns {@code true} if a {@link ReportType} with the given name exists (case-insensitive).
     *
     * @param value the name to look up
     * @return {@code true} if a matching type exists
     */
    public static boolean exists(String value) {
        return Arrays.stream(ReportType.values()).anyMatch(reportType -> reportType.name().equalsIgnoreCase(value));
    }

    /**
     * Returns the {@link ReportType} whose name matches {@code value} (case-insensitive),
     * or {@link Optional#empty()} if no match is found or {@code value} is {@code null}.
     *
     * @param value the name to look up
     * @return an {@link Optional} containing the matching type, or empty if none
     */
    public static Optional<ReportType> safeValueOf(String value) {

        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst();
    }

}
