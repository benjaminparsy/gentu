package com.benjamin.parsy.gentu.core;

import com.benjamin.parsy.gentu.core.filegenerator.text.TestReportTextFile;

import java.nio.file.Path;


/**
 * Factory for creating {@link GentuReporter} instances configured for a specific {@link ReportType}.
 */
public class GentuReporterFactory {

    private GentuReporterFactory() {
        throw new UnsupportedOperationException("GentuReporterFactory is a utility class and cannot be instantiated");
    }

    /**
     * Creates a {@link GentuReporter} for the given directory, report type, and logger.
     *
     * @param outputDirectory base output directory
     * @param reportType      the desired output format
     * @param log             logger provided by the caller
     * @return a configured {@link GentuReporter}
     * @throws IllegalArgumentException if {@code reportType} has no implementation
     */
    public static GentuReporter create(Path outputDirectory,
                                       ReportType reportType,
                                       GentuLogger log) {

        if (ReportType.TEXT.equals(reportType)) {
            return new GentuReporter(outputDirectory, new TestReportTextFile(log), log);
        }

        throw new IllegalArgumentException("The %s report type has no implementation".formatted(reportType));
    }

}
