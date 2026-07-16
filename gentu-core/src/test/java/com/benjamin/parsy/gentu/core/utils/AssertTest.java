package com.benjamin.parsy.gentu.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssertTest {

    @TempDir
    Path tempDir;

    @Test
    void constructor_shouldThrowException() throws Exception {

        // Given
        Constructor<Assert> constructor = Assert.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // When / Then
        assertThrows(Exception.class, constructor::newInstance);
    }

    @Test
    void isWritableDirectory_withValidDirectory_shouldNotThrow() {

        // When / Then
        assertDoesNotThrow(() -> Assert.isWritableDirectory(tempDir));
    }

    @Test
    void isWritableDirectory_withNull_shouldThrow() {

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> Assert.isWritableDirectory(null));
    }

    @Test
    void isWritableDirectory_withNonExistentPath_shouldThrow() {

        // Given
        Path nonExistent = tempDir.resolve("does-not-exist");

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> Assert.isWritableDirectory(nonExistent));
    }

    @Test
    void isWritableDirectory_withFile_shouldThrow() throws IOException {

        // Given
        Path file = Files.createTempFile(tempDir, "file", ".txt");

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> Assert.isWritableDirectory(file));
    }

    @Test
    void isValidType_withValidType_shouldNotThrow() {

        // When / Then
        assertDoesNotThrow(() -> Assert.isValidType("text"));
    }

    @Test
    void isValidType_withValidTypeUpperCase_shouldNotThrow() {

        // When / Then
        assertDoesNotThrow(() -> Assert.isValidType("TEXT"));
    }

    @Test
    void isValidType_withUnknownType_shouldThrow() {

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> Assert.isValidType("unknown"));
    }

    @Test
    void isValidType_withNull_shouldThrow() {

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> Assert.isValidType(null));
    }

}
