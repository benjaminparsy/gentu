package com.benjamin.parsy.gentu.core;

import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.core.filegenerator.TestReportFileWriter;
import com.benjamin.parsy.gentu.core.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GentuReporter {

    private static final Logger LOG = LoggerFactory.getLogger(GentuReporter.class);

    private final TestReportFileWriter fileWriter;
    private final Path baseDirectory;

    public GentuReporter(Path directory, TestReportFileWriter fileWriter) {
        this.baseDirectory = directory;
        this.fileWriter = fileWriter;
    }

    public void executeReporter(List<TestResult> testResults) {

        if (testResults.isEmpty()) {
            LOG.info("No tests found, report generation skipped");
            return;
        }

        Path gentuDirectory = baseDirectory.resolve(Properties.GENTU_DIRECTORY_NAME);

        try {
            FileUtils.forceCreateDirectory(gentuDirectory);
        } catch (IOException e) {
            LOG.error("Unable to create directory {}", gentuDirectory);
            return;
        }

        try {
            fileWriter.writeReport(testResults, gentuDirectory);
        } catch (Exception e) {
            LOG.error("Error while writing the report", e);
        }
    }

}
