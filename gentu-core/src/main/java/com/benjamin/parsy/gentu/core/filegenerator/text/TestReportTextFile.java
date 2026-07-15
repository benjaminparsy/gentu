package com.benjamin.parsy.gentu.core.filegenerator.text;

import com.benjamin.parsy.gentu.core.Properties;
import com.benjamin.parsy.gentu.core.downloader.TestReportFileDownloader;
import com.benjamin.parsy.gentu.core.downloader.TestReportFileDownloaderImpl;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.core.filegenerator.TestReportFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TestReportTextFile implements TestReportFileWriter {

    private static final Logger LOG = LoggerFactory.getLogger(TestReportTextFile.class);
    private static final DateTimeFormatter FILE_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String SECTION_SEPARATOR = "=".repeat(60);
    private static final String TEST_SEPARATOR = "-".repeat(60);
    private static final String FILE_EXTENSION = ".txt";

    private final TestReportFileDownloader downloader;

    public TestReportTextFile() {
        this.downloader = new TestReportFileDownloaderImpl();
    }

    @Override
    public void writeReport(List<TestResult> results, Path gentuDirectory) throws IOException {

        LocalDateTime generatedAt = LocalDateTime.now(ZoneId.systemDefault());
        String testReportFilename = Properties.TEST_REPORT_BASE_FILENAME + generatedAt.format(FILE_TIMESTAMP_FORMAT) + FILE_EXTENSION;
        Path reportFilePath = gentuDirectory.resolve(testReportFilename);

        Files.writeString(reportFilePath, buildReport(results, generatedAt));
        LOG.info("Report written: {}", reportFilePath.toAbsolutePath());

        downloader.downloadFile(results, gentuDirectory);
    }

    private String buildReport(List<TestResult> results, LocalDateTime generatedAt) {

        StringBuilder sb = new StringBuilder();
        appendHeader(sb, results, generatedAt);
        appendSummary(sb, results);
        appendDetails(sb, results);

        return sb.toString();
    }

    private void appendHeader(StringBuilder sb, List<TestResult> results, LocalDateTime generatedAt) {

        sb.append("========== TEST REPORT ==========\n");
        sb.append("Generated on : ").append(generatedAt).append("\n");
        sb.append("Number of tests : ").append(results.size()).append("\n");
        sb.append(SECTION_SEPARATOR).append("\n\n");

    }

    private void appendSummary(StringBuilder sb, List<TestResult> results) {

        sb.append(results.stream()
                .map(result -> String.format("[%d] %s%n", result.id(), result.testName()))
                .collect(Collectors.joining(
                        "",
                        "Summary :\n\n",
                        "\n" + SECTION_SEPARATOR + "\n\n")
                )
        );
    }

    private void appendDetails(StringBuilder sb, List<TestResult> results) {

        int index = 1;
        for (TestResult result : results) {
            appendTestDetail(sb, result, index++);
        }
        sb.append(SECTION_SEPARATOR).append("\n");

    }

    private void appendTestDetail(StringBuilder sb, TestResult result, int index) {

        sb.append(String.format("[%d] %s%n", index, result.testName()));
        sb.append(String.format("\tClass: %s#%s%n", result.className(), result.methodName()));
        sb.append(String.format("\tDescription: %s%n", result.description()));

        sb.append("\tGiven:\n");
        appendValues(sb, result.givenValue());

        sb.append("\tExpected:\n");
        appendValues(sb, result.expected());

        sb.append(String.format("\tExecuted at: %s%n", result.executedAt()));
        sb.append("\n").append(TEST_SEPARATOR).append("\n\n");

    }

    private void appendValues(StringBuilder sb, List<String> values) {
        for (String value : values) {
            sb.append(String.format("\t\t%s%n", value));
        }
    }

}
