package com.benjamin.parsy.gentu.core;

import com.benjamin.parsy.gentu.core.filegenerator.text.TestReportTextFile;

import java.nio.file.Path;

public class GentuReporterFactory {

    private GentuReporterFactory() {
        throw new UnsupportedOperationException("GentuReporterFactory is a utility class and cannot be instantiated");
    }

    public static GentuReporter create(Path directory) {
        return new GentuReporter(directory, new TestReportTextFile());
    }

}
