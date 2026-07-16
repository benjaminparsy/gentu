package com.benjamin.parsy.gentu.maven.reflection;

import com.benjamin.parsy.gentu.annotation.Expected;
import com.benjamin.parsy.gentu.annotation.File;
import com.benjamin.parsy.gentu.annotation.Given;
import com.benjamin.parsy.gentu.annotation.TestDescriptor;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.core.utils.StringUtils;
import com.benjamin.parsy.gentu.maven.testresearcher.ExecutedTestCase;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Loads test classes from the project's test classpath and reads
 * {@link TestDescriptor} annotations from the methods
 * that matched passing Surefire test cases.
 */
public class TestReflectionReader {

    private final List<String> classpathElements;
    private final Log log;

    /**
     * @param classpathElements test classpath entries (jars and directories) used to load test classes
     * @param log               Maven logger for warnings and errors
     */
    public TestReflectionReader(List<String> classpathElements, Log log) {
        this.classpathElements = classpathElements;
        this.log = log;
    }

    /**
     * Reads {@code @TestDescriptor} annotations from each parsed test case and builds
     * the corresponding {@link TestResult} list. Cases without the annotation are skipped.
     *
     * @param executedTestCases passing test cases extracted from Surefire reports
     * @return test results for annotated methods; never {@code null}
     */
    public List<TestResult> read(List<ExecutedTestCase> executedTestCases) throws IOException {

        URL[] urls = getClasspathElementUrl();

        List<TestResult> results = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(1);

        try (URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader())) {
            for (ExecutedTestCase parsedCase : executedTestCases) {
                readAnnotation(parsedCase, classLoader, counter).ifPresent(results::add);
            }
        }

        return results;
    }

    private URL[] getClasspathElementUrl() {

        return classpathElements.stream()
                .map(element -> {
                    try {
                        return new java.io.File(element).toURI().toURL();
                    } catch (MalformedURLException e) {
                        log.warn("Invalid classpath element: " + element);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(URL[]::new);
    }

    private Optional<TestResult> readAnnotation(ExecutedTestCase executedTestCase,
                                                URLClassLoader classLoader,
                                                AtomicInteger counter) {
        try {

            Class<?> testClass = classLoader.loadClass(executedTestCase.getClassName());
            Optional<Method> optionalMethod = findMethod(testClass, executedTestCase.getMethodName());

            return optionalMethod.map(method -> buildTestResult(method, executedTestCase, counter.getAndIncrement(), classLoader));
        } catch (ClassNotFoundException e) {
            log.warn("Cannot load test class: " + executedTestCase.getClassName());
            return Optional.empty();
        }
    }

    private Optional<Method> findMethod(Class<?> testClass, String methodName) {
        return Arrays.stream(testClass.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> m.isAnnotationPresent(TestDescriptor.class))
                .findFirst();
    }

    private TestResult buildTestResult(Method method,
                                       ExecutedTestCase executedTestCase,
                                       int id,
                                       URLClassLoader classLoader) {

        TestDescriptor descriptor = method.getAnnotation(TestDescriptor.class);
        Given given = descriptor.given();
        Expected expected = descriptor.expected();

        return new TestResult(
                id,
                descriptor.testName(),
                descriptor.description().value(),
                filterBlank(given.value()),
                parseFiles(given.files(), classLoader),
                filterBlank(expected.value()),
                simpleClassName(executedTestCase.getClassName()),
                executedTestCase.getMethodName()
        );
    }

    private List<String> filterBlank(String[] values) {
        return Arrays.stream(values)
                .filter(StringUtils::isNotEmpty)
                .toList();
    }

    private List<Path> parseFiles(File[] files, URLClassLoader classLoader) {

        return Arrays.stream(files)
                .map(file -> resolvePath(file, classLoader))
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<Path> resolvePath(File file, URLClassLoader classLoader) {

        if (StringUtils.isEmpty(file.path())) {
            return Optional.empty();
        }

        if (!file.inClasspath()) {
            return Optional.of(Path.of(file.path()).toAbsolutePath().normalize());
        }

        URL resource = classLoader.findResource(file.path());
        if (resource == null) {
            log.warn("Resource not found in classpath: " + file.path());
            return Optional.empty();
        }

        try {
            return Optional.of(Path.of(resource.toURI()).toAbsolutePath().normalize());
        } catch (URISyntaxException | IllegalArgumentException | FileSystemNotFoundException e) {
            log.warn("Cannot resolve classpath resource to a path: " + file.path()
                    + " (resolved to " + resource + ")", e);
            return Optional.empty();
        }
    }

    private String simpleClassName(String fullyQualifiedName) {
        int idx = fullyQualifiedName.lastIndexOf('.');
        return idx >= 0 ? fullyQualifiedName.substring(idx + 1) : fullyQualifiedName;
    }

}
