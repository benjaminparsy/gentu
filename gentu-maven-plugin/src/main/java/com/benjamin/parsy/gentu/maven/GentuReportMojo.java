package com.benjamin.parsy.gentu.maven;

import com.benjamin.parsy.gentu.annotation.TestDescriptor;
import com.benjamin.parsy.gentu.core.GentuReporter;
import com.benjamin.parsy.gentu.core.GentuReporterFactory;
import com.benjamin.parsy.gentu.core.ReportType;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.core.utils.Assert;
import com.benjamin.parsy.gentu.maven.reflection.TestReflectionReader;
import com.benjamin.parsy.gentu.maven.testresearcher.ExecutedTestCase;
import com.benjamin.parsy.gentu.maven.testresearcher.TestResearcher;
import com.benjamin.parsy.gentu.maven.testresearcher.TestResearcherException;
import com.benjamin.parsy.gentu.maven.testresearcher.surefire.SurefireResearcher;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Maven plugin goal {@code gentu:generate-report}: parses Surefire XML results, reads
 * {@link TestDescriptor} annotations from compiled test
 * classes, and generates a Gentu report.
 *
 * <p>Bound to the {@code verify} phase by default. Can also be invoked explicitly:
 * <pre>{@code mvn gentu:generate-report}</pre>
 */
@Mojo(
        name = "generate-report",
        defaultPhase = LifecyclePhase.VERIFY,
        requiresDependencyResolution = ResolutionScope.TEST
)
public class GentuReportMojo extends AbstractMojo {

    /**
     * Directory containing the Surefire XML reports. Skipped gracefully if it does not exist.
     */
    @Parameter(property = "surefireDirectory", defaultValue = "${project.build.directory}/surefire-reports")
    @SuppressWarnings("unused")
    private Path surefireDirectory;

    /**
     * Base output directory where the {@code gentu/} subdirectory will be created.
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}")
    @SuppressWarnings("unused")
    private Path outputDirectory;

    /**
     * Set to {@code true} to skip report generation entirely.
     */
    @Parameter(property = "skip", defaultValue = "false")
    @SuppressWarnings("unused")
    private boolean skip;

    /**
     * Output format for the report. Accepted values match {@link ReportType} names (case-insensitive).
     */
    @Parameter(property = "reportType", defaultValue = "text")
    @SuppressWarnings("unused")
    private String reportType;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    @SuppressWarnings("unused")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {

        if (skip) {
            getLog().info("Test report generation skipped.");
            return;
        }

        if (!Files.isDirectory(surefireDirectory)) {
            getLog().warn("Surefire reports directory not found, skipping.");
            return;
        }

        Assert.isWritableDirectory(outputDirectory);
        Assert.isValidType(reportType);

        TestResearcher testResearcher = new SurefireResearcher(surefireDirectory, getLog());
        List<ExecutedTestCase> executedTestCases;
        try {
            executedTestCases = testResearcher.search();
        } catch (TestResearcherException e) {
            throw new MojoExecutionException("An error occurred while searching for tests", e);
        }

        if (executedTestCases.isEmpty()) {
            getLog().info("No tests found, skipping.");
            return;
        }

        List<String> testClasspathElements;
        try {
            testClasspathElements = project.getTestClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Failed to resolve test classpath", e);
        }

        TestReflectionReader testReflectionReader = new TestReflectionReader(testClasspathElements, getLog());
        List<TestResult> testResults;
        try {
            testResults = testReflectionReader.read(executedTestCases);
        } catch (IOException e) {
            throw new MojoExecutionException("An error occurred while reading the annotated methods", e);
        }

        if (testResults.isEmpty()) {
            getLog().info("No tests annotated found, skipping.");
            return;
        }

        getLog().info("Generating test report for " + testResults.size() + " test(s)...");

        GentuReporter reporter = GentuReporterFactory.create(outputDirectory, ReportType.safeValueOf(reportType).orElseThrow(), new MavenGentuLogger(getLog()));
        reporter.executeReporter(testResults);
    }

}
