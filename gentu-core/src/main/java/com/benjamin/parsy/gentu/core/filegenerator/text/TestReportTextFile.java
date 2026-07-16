package com.benjamin.parsy.gentu.core.filegenerator.text;

import com.benjamin.parsy.gentu.core.GentuLogger;
import com.benjamin.parsy.gentu.core.Properties;
import com.benjamin.parsy.gentu.core.downloader.DownloaderException;
import com.benjamin.parsy.gentu.core.downloader.TestReportFileDownloader;
import com.benjamin.parsy.gentu.core.downloader.TestReportFileDownloaderImpl;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.core.filegenerator.ReportFileWriterException;
import com.benjamin.parsy.gentu.core.filegenerator.TestReportFileWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link TestReportFileWriter} that produces a plain-text ({@code .txt}) report and downloads
 * the associated input files into the output directory.
 */
public class TestReportTextFile implements TestReportFileWriter {

    private static final DateTimeFormatter FILE_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String SECTION_SEPARATOR = "=".repeat(60);
    private static final String TEST_SEPARATOR = "-".repeat(60);
    private static final String FILE_EXTENSION = ".txt";

    private final GentuLogger log;
    private final TestReportFileDownloader downloader;

    public TestReportTextFile(GentuLogger log) {
        this.log = log;
        this.downloader = new TestReportFileDownloaderImpl(log);
    }

    @Override
    public void writeReport(List<TestResult> results, Path gentuDirectory) throws ReportFileWriterException {

        LocalDateTime generatedAt = LocalDateTime.now(ZoneId.systemDefault());
        String testReportFilename = Properties.TEST_REPORT_BASE_FILENAME + generatedAt.format(FILE_TIMESTAMP_FORMAT) + FILE_EXTENSION;
        Path reportFilePath = gentuDirectory.resolve(testReportFilename);

        try {
            Files.writeString(reportFilePath, buildReport(results, generatedAt));
        } catch (IOException e) {
            throw new ReportFileWriterException("An error occurred while writing the report", e);
        }

        log.info("Report written: " + reportFilePath.toAbsolutePath());

        try {
            downloader.downloadFile(results, gentuDirectory);
        } catch (DownloaderException e) {
            throw new ReportFileWriterException("An error occurred while downloading the files", e);
        }
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

        sb.append("\n").append(TEST_SEPARATOR).append("\n\n");

    }

    private void appendValues(StringBuilder sb, List<String> values) {
        for (String value : values) {
            sb.append(String.format("\t\t%s%n", value));
        }
    }

}
