package com.benjamin.parsy.gentu.junit5;

import com.benjamin.parsy.gentu.core.annotation.Description;
import com.benjamin.parsy.gentu.core.annotation.Expected;
import com.benjamin.parsy.gentu.core.annotation.File;
import com.benjamin.parsy.gentu.core.annotation.Given;
import com.benjamin.parsy.gentu.core.annotation.TestDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TestDescriptorNameGeneratorTest {

    private TestDescriptorNameGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new TestDescriptorNameGenerator();
    }

    @TestDescriptor(testName = "Mon test avec annotation",
            description = @Description("my description"),
            given = @Given(
                    value = "my data",
                    file = @File(path = "/temp/anotherFile", inClasspath = true)
            ),
            expected = @Expected({
                    "my expectation1",
                    "my expectation2"
            })
    )
    void methodWithAnnotation() {
        // method with annotation
    }

    void methodWithoutAnnotation() {
        // method without annotation
    }

    @SuppressWarnings("unused")
    void methodWithParameters(String param1, int param2) {
        // method with parameters
    }

    @Test
    void generateDisplayNameForMethod_shouldReturnTestName_whenAnnotationIsPresent() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("methodWithAnnotation");

        String displayName = generator.generateDisplayNameForMethod(Collections.emptyList(), getClass(), method);

        assertEquals("Mon test avec annotation", displayName);
    }

    @Test
    void generateDisplayNameForMethod_shouldReturnAnnotationValue_notMethodName() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("methodWithAnnotation");

        String displayName = generator.generateDisplayNameForMethod(Collections.emptyList(), getClass(), method);

        assertFalse(displayName.contains("methodWithAnnotation"));
    }

    @Test
    void generateDisplayNameForMethod_shouldFallbackToStandard_whenAnnotationIsAbsent() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("methodWithoutAnnotation");

        String displayName = generator.generateDisplayNameForMethod(Collections.emptyList(), getClass(), method);

        assertEquals("methodWithoutAnnotation()", displayName);
    }

    @Test
    void generateDisplayNameForMethod_shouldFallbackToStandard_withParameters() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("methodWithParameters", String.class, int.class);

        String displayName = generator.generateDisplayNameForMethod(Collections.emptyList(), getClass(), method);

        assertEquals("methodWithParameters(String, int)", displayName);
    }

    @Test
    void generateDisplayNameForMethod_shouldFallbackToStandard_withEnclosingTypes() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("methodWithoutAnnotation");

        String displayName = generator.generateDisplayNameForMethod(List.of(getClass()), getClass(), method);

        assertEquals("methodWithoutAnnotation()", displayName);
    }

}