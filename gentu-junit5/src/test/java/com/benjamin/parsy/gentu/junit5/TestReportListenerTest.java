package com.benjamin.parsy.gentu.junit5;

import com.benjamin.parsy.gentu.core.GentuReporter;
import com.benjamin.parsy.gentu.core.GentuReporterFactory;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.junit5.dto.TestResultRegistry;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.TestPlan;
import org.mockito.MockedStatic;

import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class TestReportListenerTest {

    private final TestReportListener listener = new TestReportListener();

    @Test
    void testPlanExecutionStarted_shouldClearTheRegistry() {
        try (MockedStatic<TestResultRegistry> registry = mockStatic(TestResultRegistry.class)) {
            listener.testPlanExecutionStarted(mock(TestPlan.class));

            registry.verify(TestResultRegistry::clear);
        }
    }

    @Test
    void testPlanExecutionFinished_withEmptyResults_shouldNotCreateTheReporter() {
        try (MockedStatic<TestResultRegistry> registry = mockStatic(TestResultRegistry.class);
             MockedStatic<GentuReporterFactory> factory = mockStatic(GentuReporterFactory.class)) {

            registry.when(TestResultRegistry::getAll).thenReturn(List.of());

            listener.testPlanExecutionFinished(mock(TestPlan.class));

            factory.verifyNoInteractions();
        }
    }

    @Test
    void testPlanExecutionFinished_withResults_shouldCreateAndExecuteTheReporter() {
        List<TestResult> results = List.of(mock(TestResult.class));
        GentuReporter reporterMock = mock(GentuReporter.class);

        try (MockedStatic<TestResultRegistry> registry = mockStatic(TestResultRegistry.class);
             MockedStatic<GentuReporterFactory> factory = mockStatic(GentuReporterFactory.class)) {

            registry.when(TestResultRegistry::getAll).thenReturn(results);
            factory.when(() -> GentuReporterFactory.create(Path.of("target")))
                    .thenReturn(reporterMock);

            listener.testPlanExecutionFinished(mock(TestPlan.class));

            factory.verify(() -> GentuReporterFactory.create(Path.of("target")));
            verify(reporterMock).executeReporter(results);
        }
    }

}