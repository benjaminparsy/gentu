package com.benjamin.parsy.gentu.core;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GentuReporterFactoryTest {

    @Test
    void create_shouldReturnGentuReporter() {

        // Given
        Path directory = Path.of("target", "gentu-report");

        // When
        GentuReporter reporter = GentuReporterFactory.create(directory, ReportType.TEXT, GentuLogger.noOp());

        // Then
        assertNotNull(reporter);
    }

    @Test
    void constructor_shouldThrowException() throws Exception {

        // Given
        Constructor<GentuReporterFactory> constructor = GentuReporterFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // When / Then
        assertThrows(Exception.class, constructor::newInstance);
    }

}
