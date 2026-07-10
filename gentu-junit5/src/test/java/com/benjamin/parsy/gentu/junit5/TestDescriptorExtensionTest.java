package com.benjamin.parsy.gentu.junit5;

import com.benjamin.parsy.gentu.core.annotation.Description;
import com.benjamin.parsy.gentu.core.annotation.Expected;
import com.benjamin.parsy.gentu.core.annotation.File;
import com.benjamin.parsy.gentu.core.annotation.Given;
import com.benjamin.parsy.gentu.core.annotation.TestDescriptor;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.junit5.dto.TestResultRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestDescriptorExtensionTest {

    private TestDescriptorExtension extension;
    private ExtensionContext context;

    @BeforeEach
    void setUp() {
        extension = new TestDescriptorExtension();
        context = mock(ExtensionContext.class);
        TestResultRegistry.clear();
    }

    @Test
    void afterTestExecution_withException_shouldDoNothing() {

        // Given
        when(context.getExecutionException()).thenReturn(Optional.of(new RuntimeException()));

        // When
        assertDoesNotThrow(() -> extension.afterTestExecution(context));

        // Then
        assertTrue(TestResultRegistry.getAll().isEmpty());
    }

    @Test
    void afterTestExecution_withoutAnnotation_shouldDoNothing() throws Exception {

        // Given
        Method method = this.getClass().getDeclaredMethod("methodWithoutAnnotation");

        when(context.getExecutionException()).thenReturn(Optional.empty());
        when(context.getRequiredTestMethod()).thenReturn(method);

        // When
        assertDoesNotThrow(() -> extension.afterTestExecution(context));

        // Then
        assertTrue(TestResultRegistry.getAll().isEmpty());
    }

    @Test
    void afterTestExecution_withAnnotation_shouldRegisterResult() throws Exception {

        // Given
        Method method = this.getClass().getDeclaredMethod("annotatedMethod");

        when(context.getExecutionException()).thenReturn(Optional.empty());
        when(context.getRequiredTestMethod()).thenReturn(method);
        when(context.getRequiredTestClass()).then(invocationOnMock -> this.getClass());

        // When
        assertDoesNotThrow(() -> extension.afterTestExecution(context));

        // Then
        assertEquals(1, TestResultRegistry.getAll().size());

        TestResult result = TestResultRegistry.getAll().getFirst();

        assertEquals("monTest", result.testName());
        assertEquals("description test", result.description());
        assertEquals(this.getClass().getSimpleName(), result.className());
        assertEquals("annotatedMethod", result.methodName());
    }

    @Test
    void afterTestExecution_withAnnotation_shouldFilterEmptyStrings() throws Exception {

        // Given
        Method method = this.getClass().getDeclaredMethod("annotatedMethod");

        when(context.getExecutionException()).thenReturn(Optional.empty());
        when(context.getRequiredTestMethod()).thenReturn(method);
        when(context.getRequiredTestClass()).then(invocationOnMock -> this.getClass());

        // When
        assertDoesNotThrow(() -> extension.afterTestExecution(context));

        // Then
        TestResult result = TestResultRegistry.getAll().getFirst();

        assertEquals(2, result.givenValue().size());
        assertEquals(1, result.givenFiles().size());
        assertEquals(1, result.expected().size());
    }

    void methodWithoutAnnotation() {
        // method without annotation
    }

    @TestDescriptor(
            testName = "monTest",
            description = @Description("description test"),
            given = @Given(
                    value = {"data1", "", "data2"},
                    file = @File(path = "file.json", inClasspath = true)
            ),
            expected = @Expected({"result1", ""})
    )
    void annotatedMethod() {
        // annotatedMethod
    }

}