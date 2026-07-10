package com.benjamin.parsy.gentu.junit5;

import com.benjamin.parsy.gentu.core.GentuReporter;
import com.benjamin.parsy.gentu.core.GentuReporterFactory;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.junit5.dto.TestResultRegistry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

import java.nio.file.Path;
import java.util.List;

public class TestReportListener implements TestExecutionListener {

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        TestResultRegistry.clear();
        TestExecutionListener.super.testPlanExecutionStarted(testPlan);
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {

        List<TestResult> testResults = TestResultRegistry.getAll();
        if (testResults.isEmpty()) {
            return;
        }

        GentuReporter gentuReporter = GentuReporterFactory.create(Path.of("target"));
        gentuReporter.executeReporter(testResults);

    }

}
