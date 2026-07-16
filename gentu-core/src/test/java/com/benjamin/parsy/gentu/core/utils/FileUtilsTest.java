package com.benjamin.parsy.gentu.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void constructor_shouldThrowException() throws Exception {

        // Given
        Constructor<FileUtils> constructor = FileUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // When / Then
        assertThrows(Exception.class, constructor::newInstance);
    }

    @Test
    void isDirectory_withNull_shouldReturnFalse() {

        // When
        boolean result = FileUtils.isDirectory(null);

        // Then
        assertFalse(result);
    }

    @Test
    void isDirectory_withNonExistentPath_shouldReturnFalse() {

        // Given
        Path nonExistent = tempDir.resolve("does-not-exist");

        // When
        boolean result = FileUtils.isDirectory(nonExistent);

        // Then
        assertFalse(result);
    }

    @Test
    void isDirectory_withFile_shouldReturnFalse() throws IOException {

        // Given
        Path file = Files.createTempFile(tempDir, "file", ".txt");

        // When
        boolean result = FileUtils.isDirectory(file);

        // Then
        assertFalse(result);
    }

    @Test
    void isDirectory_withDirectory_shouldReturnTrue() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");

        // When
        boolean result = FileUtils.isDirectory(directory);

        // Then
        assertTrue(result);
    }

    @Test
    void deleteContent_withNonDirectory_shouldThrowIOException() throws IOException {

        // Given
        Path file = Files.createTempFile(tempDir, "file", ".txt");

        // When / Then
        assertThrows(IOException.class, () -> FileUtils.deleteContent(file));
    }

    @Test
    void deleteContent_withEmptyDirectory_shouldKeepDirectory() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");

        // When
        FileUtils.deleteContent(directory);

        // Then
        assertTrue(Files.isDirectory(directory));
    }

    @Test
    void deleteContent_withFiles_shouldDeleteAllFilesButKeepRoot() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");
        Files.createTempFile(directory, "file1", ".txt");
        Files.createTempFile(directory, "file2", ".txt");

        // When
        FileUtils.deleteContent(directory);

        // Then
        assertTrue(Files.isDirectory(directory));
        try (var stream = Files.list(directory)) {
            assertEquals(0, stream.count());
        }
    }

    @Test
    void deleteContent_withSubdirectory_shouldDeleteSubdirectoryButKeepRoot() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");
        Path subdir = Files.createTempDirectory(directory, "subdir");

        // When
        FileUtils.deleteContent(directory);

        // Then
        assertTrue(Files.isDirectory(directory));
        assertFalse(Files.exists(subdir));
    }

    @Test
    void deleteContent_withNestedContent_shouldDeleteAllContentButKeepRoot() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");
        Path subdir = Files.createTempDirectory(directory, "subdir");
        Files.createTempFile(directory, "file", ".txt");
        Files.createTempFile(subdir, "nested-file", ".txt");

        // When
        FileUtils.deleteContent(directory);

        // Then
        assertTrue(Files.isDirectory(directory));
        try (var stream = Files.list(directory)) {
            assertEquals(0, stream.count());
        }
    }

    @Test
    void deleteRecursively_withEmptyDirectory_shouldKeepDirectory() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");

        // When
        FileUtils.deleteRecursively(directory);

        // Then
        assertTrue(Files.isDirectory(directory));
    }

    @Test
    void deleteRecursively_withFiles_shouldDeleteAllFiles() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");
        Path file1 = Files.createTempFile(directory, "file1", ".txt");
        Path file2 = Files.createTempFile(directory, "file2", ".txt");

        // When
        FileUtils.deleteRecursively(directory);

        // Then
        assertFalse(Files.exists(file1));
        assertFalse(Files.exists(file2));
    }

    @Test
    void deleteRecursively_withSubdirectory_shouldDeleteSubdirectory() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");
        Path subdir = Files.createTempDirectory(directory, "subdir");

        // When
        FileUtils.deleteRecursively(directory);

        // Then
        assertFalse(Files.exists(subdir));
    }

    @Test
    void deleteRecursively_withNestedContent_shouldDeleteAllContent() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");
        Path subdir = Files.createTempDirectory(directory, "subdir");
        Path file = Files.createTempFile(directory, "file", ".txt");
        Path nestedFile = Files.createTempFile(subdir, "nested-file", ".txt");

        // When
        FileUtils.deleteRecursively(directory);

        // Then
        assertFalse(Files.exists(nestedFile));
        assertFalse(Files.exists(subdir));
        assertFalse(Files.exists(file));
    }

    @Test
    void forceCreateDirectory_withNonExistingDirectory_shouldCreateDirectory() throws IOException {

        // Given
        Path directory = tempDir.resolve("new-directory");

        // When
        FileUtils.forceCreateDirectory(directory);

        // Then
        assertTrue(Files.isDirectory(directory));
    }

    @Test
    void forceCreateDirectory_withExistingEmptyDirectory_shouldKeepDirectory() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");

        // When
        FileUtils.forceCreateDirectory(directory);

        // Then
        assertTrue(Files.isDirectory(directory));
    }

    @Test
    void forceCreateDirectory_withExistingDirectoryWithFiles_shouldClearContent() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");
        Files.createTempFile(directory, "file", ".txt");

        // When
        FileUtils.forceCreateDirectory(directory);

        // Then
        assertTrue(Files.isDirectory(directory));
        try (var stream = Files.list(directory)) {
            assertEquals(0, stream.count());
        }
    }

    @Test
    void forceCreateDirectory_withExistingDirectoryWithSubdirectory_shouldClearContent() throws IOException {

        // Given
        Path directory = Files.createTempDirectory(tempDir, "dir");
        Path subdir = Files.createTempDirectory(directory, "subdir");
        Files.createTempFile(subdir, "nested-file", ".txt");

        // When
        FileUtils.forceCreateDirectory(directory);

        // Then
        assertTrue(Files.isDirectory(directory));
        assertFalse(Files.exists(subdir));
    }

}
