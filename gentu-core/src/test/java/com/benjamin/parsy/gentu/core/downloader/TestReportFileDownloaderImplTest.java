package com.benjamin.parsy.gentu.core.downloader;

import com.benjamin.parsy.gentu.core.dto.GivenFile;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportFileDownloaderImplTest {

    private final TestReportFileDownloaderImpl downloader = new TestReportFileDownloaderImpl();

    @TempDir
    Path tempDir;

    @Test
    void downloadFile_withEmptyList_shouldNotCreateAnyDirectory() throws IOException {

        // Given
        List<TestResult> results = Collections.emptyList();

        // When
        downloader.downloadFile(results, tempDir);

        // Then
        try (var stream = Files.list(tempDir)) {
            assertEquals(0, stream.count());
        }
    }

    @Test
    void downloadFile_withResultWithoutFiles_shouldCreateTargetDirectory() {

        // Given
        TestResult result = buildTestResult(1, Collections.emptyList());

        // When
        assertDoesNotThrow(() -> downloader.downloadFile(List.of(result), tempDir));

        // Then
        assertTrue(Files.isDirectory(tempDir.resolve("test_1")));
    }

    @Test
    void downloadFile_withMultipleResults_shouldCreateOneDirectoryPerResult() {

        // Given
        List<TestResult> results = List.of(
                buildTestResult(1, Collections.emptyList()),
                buildTestResult(2, Collections.emptyList()),
                buildTestResult(3, Collections.emptyList())
        );

        // When
        assertDoesNotThrow(() -> downloader.downloadFile(results, tempDir));

        // Then
        assertTrue(Files.isDirectory(tempDir.resolve("test_1")));
        assertTrue(Files.isDirectory(tempDir.resolve("test_2")));
        assertTrue(Files.isDirectory(tempDir.resolve("test_3")));
    }

    @Test
    void downloadFile_withClasspathGivenFile_shouldCopyFileToTargetDirectory() {

        // Given
        TestResult result = buildTestResult(1, List.of(new GivenFile("testFile.txt", true)));

        // When
        downloader.downloadFile(List.of(result), tempDir);

        // Then
        assertTrue(Files.exists(tempDir.resolve("test_1").resolve("testFile.txt")));
    }

    @Test
    void downloadFile_withClasspathGivenFileWithLeadingSlash_shouldCopyFileToTargetDirectory() {

        // Given
        TestResult result = buildTestResult(1, List.of(new GivenFile("testFile.txt", true)));

        // When
        downloader.downloadFile(List.of(result), tempDir);

        // Then
        assertTrue(Files.exists(tempDir.resolve("test_1").resolve("testFile.txt")));
    }

    @Test
    void downloadFile_withAbsolutePathGivenFile_shouldCopyFileToTargetDirectory() throws IOException {

        // Given
        Path sourceFile = Files.createTempFile(tempDir, "source", ".txt");
        TestResult result = buildTestResult(1, List.of(new GivenFile(sourceFile.toString(), false)));

        // When
        downloader.downloadFile(List.of(result), tempDir);

        // Then
        assertTrue(Files.exists(tempDir.resolve("test_1").resolve(sourceFile.getFileName())));
    }

    @Test
    void downloadFile_withClasspathFileNotFound_shouldNotThrowAndStillCreateDirectory() {

        // Given
        TestResult result = buildTestResult(1, List.of(new GivenFile("classpath:nonexistent.txt", true)));

        // When
        assertDoesNotThrow(() -> downloader.downloadFile(List.of(result), tempDir));

        // Then
        assertTrue(Files.isDirectory(tempDir.resolve("test_1")));
    }

    private TestResult buildTestResult(int id, List<GivenFile> givenFiles) {
        return new TestResult(
                id,
                "testName",
                "desc",
                Collections.emptyList(),
                givenFiles,
                Collections.emptyList(),
                "TestClass",
                "testMethod",
                LocalDate.of(2026, Month.JULY, 9).atStartOfDay()
        );
    }

}
