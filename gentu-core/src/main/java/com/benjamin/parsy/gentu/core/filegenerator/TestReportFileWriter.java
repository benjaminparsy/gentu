package com.benjamin.parsy.gentu.core.filegenerator;

import com.benjamin.parsy.gentu.core.dto.TestResult;

import java.nio.file.Path;
import java.util.List;

/**
 * Writes a test report from a list of {@link TestResult} instances to a given output directory.
 */
public interface TestReportFileWriter {

    /**
     * Writes the report to the specified directory.
     *
     * @param results   the test results to include in the report
     * @param directory the directory where the report file will be created
     * @throws ReportFileWriterException if an error occurs while writing the report
     */
    void writeReport(List<TestResult> results, Path directory) throws ReportFileWriterException;

}
