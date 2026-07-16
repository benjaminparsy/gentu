package com.benjamin.parsy.gentu.core;

import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.core.filegenerator.ReportFileWriterException;
import com.benjamin.parsy.gentu.core.filegenerator.TestReportFileWriter;
import com.benjamin.parsy.gentu.core.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Orchestrates report generation: prepares the Gentu output directory and delegates writing
 * to a {@link com.benjamin.parsy.gentu.core.filegenerator.TestReportFileWriter}.
 */
public class GentuReporter {

    private final GentuLogger log;
    private final TestReportFileWriter fileWriter;
    private final Path baseDirectory;

    /**
     * Creates a reporter that writes to a {@code gentu} subdirectory inside {@code directory}.
     *
     * @param directory  base output directory (typically {@code target/})
     * @param fileWriter strategy used to produce the report file
     * @param log        logger provided by the caller
     */
    public GentuReporter(Path directory, TestReportFileWriter fileWriter, GentuLogger log) {
        this.baseDirectory = directory;
        this.fileWriter = fileWriter;
        this.log = log;
    }

    /**
     * Generates the report from the given test results.
     * Does nothing if the list is empty.
     *
     * @param testResults the test results to include in the report
     */
    public void executeReporter(List<TestResult> testResults) {

        if (testResults.isEmpty()) {
            log.info("No tests found, report generation skipped");
            return;
        }

        Path gentuDirectory = baseDirectory.resolve(Properties.GENTU_DIRECTORY_NAME);

        try {
            FileUtils.forceCreateDirectory(gentuDirectory);
        } catch (IOException e) {
            log.error("Unable to create directory " + gentuDirectory, e);
            return;
        }

        try {
            fileWriter.writeReport(testResults, gentuDirectory);
        } catch (ReportFileWriterException e) {
            log.error("Error while writing the report", e);
        }
    }

}
