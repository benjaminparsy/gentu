package com.benjamin.parsy.gentu.core.filegenerator.text;

import com.benjamin.parsy.gentu.core.GentuLogger;
import com.benjamin.parsy.gentu.core.Properties;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.core.filegenerator.ReportFileWriterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportTextFileTest {

    private final TestReportTextFile testReportTextFile = new TestReportTextFile(GentuLogger.noOp());

    @TempDir
    Path tempDir;

    @Test
    void writeReport_withSingleResult_shouldCreateFile() throws IOException {

        // Given
        List<TestResult> testResultList = List.of(buildTestResult());

        // When
        assertDoesNotThrow(() -> testReportTextFile.writeReport(testResultList, tempDir));

        // Then
        Optional<Path> reportFile = findReportFile();
        assertTrue(reportFile.isPresent());
        assertTrue(reportFile.get().getFileName().toString().startsWith(Properties.TEST_REPORT_BASE_FILENAME));
        assertTrue(reportFile.get().getFileName().toString().endsWith(".txt"));
    }

    @Test
    void writeReport_withSingleResult_shouldMatchExpectedReport() throws IOException, ReportFileWriterException {

        // Given
        List<TestResult> testResultList = List.of(buildTestResult());

        // When
        testReportTextFile.writeReport(testResultList, tempDir);

        // Then
        String actual = normalizeReport(readReport());

        try (InputStream is = Objects.requireNonNull(getClass().getResourceAsStream("/expected-report.txt"))) {
            String expected = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(expected, actual);
        }
    }

    @Test
    void writeReport_withMultipleResults_shouldContainTestCountInHeader() throws IOException {

        // Given
        List<TestResult> testResultList = List.of(
                buildTestResult(1, "firstTest"),
                buildTestResult(2, "secondTest")
        );

        // When
        assertDoesNotThrow(() -> testReportTextFile.writeReport(testResultList, tempDir));

        // Then
        assertTrue(readReport().contains("Number of tests : 2"));
    }

    @Test
    void writeReport_withMultipleResults_shouldNumberAllTestsInSummary() throws IOException {

        // Given
        List<TestResult> testResultList = List.of(
                buildTestResult(1, "firstTest"),
                buildTestResult(2, "secondTest")
        );

        // When
        assertDoesNotThrow(() -> testReportTextFile.writeReport(testResultList, tempDir));

        // Then
        String content = readReport();
        assertTrue(content.contains("[1] firstTest"));
        assertTrue(content.contains("[2] secondTest"));
    }

    @Test
    void writeReport_withEmptyList_shouldWriteZeroTestsInHeader() throws IOException {

        // Given
        List<TestResult> testResultList = Collections.emptyList();

        // When
        assertDoesNotThrow(() -> testReportTextFile.writeReport(testResultList, tempDir));

        // Then
        assertTrue(readReport().contains("Number of tests : 0"));
    }

    private String normalizeReport(String content) {
        return content
                .replace("\r\n", "\n")
                .replaceAll("Generated on : .*", "Generated on : {generatedAt}");
    }

    private String readReport() throws IOException {
        Path reportFile = findReportFile().orElseThrow();
        return Files.readString(reportFile);
    }

    private Optional<Path> findReportFile() throws IOException {
        try (var stream = Files.list(tempDir)) {
            return stream
                    .filter(p -> p.getFileName().toString().startsWith(Properties.TEST_REPORT_BASE_FILENAME))
                    .findFirst();
        }
    }

    private TestResult buildTestResult() {
        return buildTestResult(1, "testName_withData_shouldDoSomething");
    }

    private TestResult buildTestResult(int id, String testName) {
        return new TestResult(
                id,
                testName,
                "desc",
                List.of("given1", "given2"),
                Collections.emptyList(),
                List.of("expected1", "expected2"),
                "TestClass",
                "testMethod"
        );
    }

}
