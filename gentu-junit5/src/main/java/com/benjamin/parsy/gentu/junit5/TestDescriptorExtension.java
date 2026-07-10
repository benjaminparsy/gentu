package com.benjamin.parsy.gentu.junit5;

import com.benjamin.parsy.gentu.core.annotation.Description;
import com.benjamin.parsy.gentu.core.annotation.Expected;
import com.benjamin.parsy.gentu.core.annotation.File;
import com.benjamin.parsy.gentu.core.annotation.Given;
import com.benjamin.parsy.gentu.core.annotation.TestDescriptor;
import com.benjamin.parsy.gentu.core.dto.GivenFile;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.junit5.dto.TestResultRegistry;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TestDescriptorExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {

        if (context.getExecutionException().isPresent()) {
            return;
        }

        Method method = context.getRequiredTestMethod();

        TestDescriptor descriptor = method.getAnnotation(TestDescriptor.class);
        if (descriptor == null) {
            return;
        }

        Given given = descriptor.given();
        Expected expected = descriptor.expected();
        Description desc = descriptor.description();

        TestResult result = new TestResult(
                TestResultRegistry.size() + 1,
                descriptor.testName(),
                desc.value(),
                filterEmptyList(given.value()),
                parseFileDescriptor(given.file()),
                filterEmptyList(expected.value()),
                context.getRequiredTestClass().getSimpleName(),
                method.getName(),
                LocalDateTime.now()
        );

        TestResultRegistry.add(result);
    }

    private List<String> filterEmptyList(String[] values) {
        return Arrays.stream(values)
                .filter(StringUtils::isNotBlank)
                .toList();
    }

    private List<GivenFile> parseFileDescriptor(File[] filesDescriptor) {

        return Arrays.stream(filesDescriptor).filter(Objects::nonNull)
                .map(fileDescriptor -> new GivenFile(fileDescriptor.path(), fileDescriptor.inClasspath()))
                .toList();
    }

}
