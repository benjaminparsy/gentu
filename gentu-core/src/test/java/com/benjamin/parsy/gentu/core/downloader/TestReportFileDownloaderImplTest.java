package com.benjamin.parsy.gentu.core.downloader;

import com.benjamin.parsy.gentu.core.GentuLogger;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportFileDownloaderImplTest {

    private final TestReportFileDownloaderImpl downloader = new TestReportFileDownloaderImpl(GentuLogger.noOp());

    @TempDir
    Path tempDir;

    @Test
    void downloadFile_withEmptyList_shouldNotCreateAnyDirectory() throws IOException, DownloaderException {

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
    void downloadFile_withResultWithoutFiles_shouldNotCreateDirectory() {

        // Given
        TestResult result = buildTestResult(1, List.of());

        // When
        assertDoesNotThrow(() -> downloader.downloadFile(List.of(result), tempDir));

        // Then
        assertFalse(Files.exists(tempDir.resolve("test_1")));
    }

    @Test
    void downloadFile_withMultipleResults_shouldCopyFilesForEachResult() throws IOException {

        // Given
        Path file1 = Files.createTempFile(tempDir, "file1", ".txt");
        Path file2 = Files.createTempFile(tempDir, "file2", ".txt");
        List<TestResult> results = List.of(
                buildTestResult(1, List.of(file1)),
                buildTestResult(2, List.of(file2))
        );

        // When
        assertDoesNotThrow(() -> downloader.downloadFile(results, tempDir));

        // Then
        assertTrue(Files.exists(tempDir.resolve("test_1").resolve(file1.getFileName())));
        assertTrue(Files.exists(tempDir.resolve("test_2").resolve(file2.getFileName())));
    }

    @Test
    void downloadFile_withExistingFile_shouldCopyFileToTargetDirectory() throws IOException, DownloaderException {

        // Given
        Path sourceFile = Files.createTempFile(tempDir, "source", ".txt");
        TestResult result = buildTestResult(1, List.of(sourceFile));

        // When
        downloader.downloadFile(List.of(result), tempDir);

        // Then
        assertTrue(Files.exists(tempDir.resolve("test_1").resolve(sourceFile.getFileName())));
    }

    @Test
    void downloadFile_withNonExistentFile_shouldThrowDownloaderException() {

        // Given
        Path nonExistent = tempDir.resolve("does-not-exist.txt");
        TestResult result = buildTestResult(1, List.of(nonExistent));

        // When / Then
        assertThrows(DownloaderException.class, () -> downloader.downloadFile(List.of(result), tempDir));
    }

    private TestResult buildTestResult(int id, List<Path> files) {
        return new TestResult(
                id,
                "testName",
                "desc",
                Collections.emptyList(),
                files,
                Collections.emptyList(),
                "TestClass",
                "testMethod"
        );
    }

}
