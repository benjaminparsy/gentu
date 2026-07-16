package com.benjamin.parsy.gentu.maven.reflection;

import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.maven.fixture.SampleAnnotatedClass;
import com.benjamin.parsy.gentu.maven.testresearcher.ExecutedTestCase;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestDescriptorReaderTestReflection {

    private TestReflectionReader reader;

    @BeforeEach
    void setUp() {
        List<String> classpath = Arrays.asList(System.getProperty("java.class.path").split(java.io.File.pathSeparator));
        reader = new TestReflectionReader(classpath, new SystemStreamLog());
    }

    @Test
    void read_withAnnotatedMethod_shouldReturnTestResult() throws Exception {

        // Given
        List<ExecutedTestCase> input = List.of(
                new ExecutedTestCase(SampleAnnotatedClass.class.getName(), "annotatedMethod") {
                }
        );

        // When
        List<TestResult> results = reader.read(input);

        // Then
        assertEquals(1, results.size());
        TestResult result = results.getFirst();
        assertEquals(1, result.id());
        assertEquals("Sample test — should do something", result.testName());
        assertEquals("Verifies the core behavior", result.description());
        assertEquals("SampleAnnotatedClass", result.className());
        assertEquals("annotatedMethod", result.methodName());
    }

    @Test
    void read_withAnnotatedMethod_shouldFilterBlankGivenValues() throws Exception {

        // Given
        List<ExecutedTestCase> input = List.of(
                new ExecutedTestCase(SampleAnnotatedClass.class.getName(), "annotatedMethod") {
                }
        );

        // When
        List<TestResult> results = reader.read(input);

        // Then
        assertEquals(2, results.getFirst().givenValue().size());
        assertTrue(results.getFirst().givenValue().contains("input A"));
        assertTrue(results.getFirst().givenValue().contains("input B"));
    }

    @Test
    void read_withAnnotatedMethod_shouldFilterBlankExpectedValues() throws Exception {

        // Given
        List<ExecutedTestCase> input = List.of(
                new ExecutedTestCase(SampleAnnotatedClass.class.getName(), "annotatedMethod") {
                }
        );

        // When
        List<TestResult> results = reader.read(input);

        // Then
        assertEquals(1, results.getFirst().expected().size());
        assertEquals("output A", results.getFirst().expected().getFirst());
    }

    @Test
    void read_withAnnotatedMethod_shouldResolveClasspathFileToAbsolutePath() throws Exception {

        // Given
        List<ExecutedTestCase> input = List.of(
                new ExecutedTestCase(SampleAnnotatedClass.class.getName(), "annotatedMethod") {
                }
        );

        // When
        List<TestResult> results = reader.read(input);

        // Then
        assertEquals(1, results.getFirst().absoluteFilePaths().size());
        assertEquals("data.json", results.getFirst().absoluteFilePaths().getFirst().getFileName().toString());
    }

    @Test
    void read_withMultipleAnnotatedMethods_shouldReturnAllWithSequentialIds() throws Exception {

        // Given
        List<ExecutedTestCase> input = List.of(
                new ExecutedTestCase(SampleAnnotatedClass.class.getName(), "annotatedMethod") {
                },
                new ExecutedTestCase(SampleAnnotatedClass.class.getName(), "anotherAnnotatedMethod") {
                }
        );

        // When
        List<TestResult> results = reader.read(input);

        // Then
        assertEquals(2, results.size());
        assertEquals(1, results.get(0).id());
        assertEquals(2, results.get(1).id());
        assertEquals("Another test", results.get(1).testName());
    }

    @Test
    void read_withUnannotatedMethod_shouldReturnEmpty() throws Exception {

        // Given
        List<ExecutedTestCase> input = List.of(
                new ExecutedTestCase(SampleAnnotatedClass.class.getName(), "unannotatedMethod") {
                }
        );

        // When
        List<TestResult> results = reader.read(input);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    void read_withUnknownClass_shouldReturnEmpty() throws Exception {

        // Given
        List<ExecutedTestCase> input = List.of(
                new ExecutedTestCase("com.example.NonExistentClass", "someMethod") {
                }
        );

        // When
        List<TestResult> results = reader.read(input);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    void read_withUnknownMethod_shouldReturnEmpty() throws Exception {

        // Given
        List<ExecutedTestCase> input = List.of(
                new ExecutedTestCase(SampleAnnotatedClass.class.getName(), "nonExistentMethod") {
                }
        );

        // When
        List<TestResult> results = reader.read(input);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    void read_withEmptyInput_shouldReturnEmpty() throws Exception {

        // Given
        List<ExecutedTestCase> input = List.of();

        // When
        List<TestResult> results = reader.read(input);

        // Then
        assertTrue(results.isEmpty());
    }

}
