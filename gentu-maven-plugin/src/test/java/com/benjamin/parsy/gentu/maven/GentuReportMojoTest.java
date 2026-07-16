package com.benjamin.parsy.gentu.maven;

import com.benjamin.parsy.gentu.maven.fixture.SampleAnnotatedClass;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GentuReportMojoTest {

    @TempDir
    Path tempDir;

    @Mock
    private MavenProject mavenProject;

    private GentuReportMojo mojo;

    @BeforeEach
    void setUp() throws Exception {
        mojo = new GentuReportMojo();
        setField("project", mavenProject);
        setField("outputDirectory", tempDir);
        setField("skip", false);
        setField("reportType", "text");
    }

    @Test
    void execute_whenSkipped_shouldDoNothing() throws Exception {

        // Given
        setField("skip", true);

        // When
        assertDoesNotThrow(() -> mojo.execute());

        // Then
        verifyNoInteractions(mavenProject);
    }

    @Test
    void execute_whenSurefireDirectoryDoesNotExist_shouldSkip() throws Exception {

        // Given
        setField("surefireDirectory", Path.of("/non/existent/path"));

        // When
        assertDoesNotThrow(() -> mojo.execute());

        // Then
        verifyNoInteractions(mavenProject);
    }

    @Test
    void execute_whenNoXmlFilesInSurefireDirectory_shouldSkip() throws Exception {

        // Given
        setField("surefireDirectory", tempDir);

        // When
        assertDoesNotThrow(() -> mojo.execute());

        // Then
        verifyNoInteractions(mavenProject);
    }

    @Test
    void execute_whenTestsHaveNoAnnotation_shouldNotGenerateReport() throws Exception {

        // Given
        Path surefireDir = tempDir.resolve("surefire-reports");
        Files.createDirectory(surefireDir);
        Files.writeString(surefireDir.resolve("TEST-Foo.xml"), """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="1" failures="0" errors="0" skipped="0">
                  <testcase name="someTest" classname="com.example.UnknownClass" time="0.01"/>
                </testsuite>
                """);
        setField("surefireDirectory", surefireDir);
        List<String> classpath = Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator));
        when(mavenProject.getTestClasspathElements()).thenReturn(classpath);

        // When
        assertDoesNotThrow(() -> mojo.execute());

        // Then
        Path gentuDir = tempDir.resolve("gentu");
        assertTrue(!Files.exists(gentuDir) || Files.list(gentuDir).findAny().isEmpty());
    }

    @Test
    void execute_whenPassingTestsHaveNoAnnotation_shouldSkip() throws Exception {

        // Given
        Path surefireDir = tempDir.resolve("surefire-reports");
        Files.createDirectory(surefireDir);
        Files.writeString(surefireDir.resolve("TEST-Foo.xml"), """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="1" failures="0" errors="0" skipped="0">
                  <testcase name="unannotatedMethod" classname="%s" time="0.01"/>
                </testsuite>
                """.formatted(SampleAnnotatedClass.class.getName()));
        setField("surefireDirectory", surefireDir);
        List<String> classpath = Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator));
        when(mavenProject.getTestClasspathElements()).thenReturn(classpath);

        // When
        assertDoesNotThrow(() -> mojo.execute());

        // Then
        Path gentuDir = tempDir.resolve("gentu");
        assertTrue(!Files.exists(gentuDir) || Files.list(gentuDir).findAny().isEmpty());
    }

    @Test
    void execute_withAnnotatedPassingTests_shouldGenerateReport() throws Exception {

        // Given
        Path surefireDir = tempDir.resolve("surefire-reports");
        Files.createDirectory(surefireDir);
        Files.writeString(surefireDir.resolve("TEST-Sample.xml"), """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="1" failures="0" errors="0" skipped="0">
                  <testcase name="annotatedMethod" classname="%s" time="0.01"/>
                </testsuite>
                """.formatted(SampleAnnotatedClass.class.getName()));
        setField("surefireDirectory", surefireDir);
        List<String> classpath = Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator));
        when(mavenProject.getTestClasspathElements()).thenReturn(classpath);

        // When
        mojo.execute();

        // Then
        Path gentuDir = tempDir.resolve("gentu");
        assertTrue(Files.exists(gentuDir), "gentu directory should be created");
        assertTrue(Files.list(gentuDir).anyMatch(p -> p.getFileName().toString().startsWith("test-report_")),
                "report file should be generated");
    }

    private void setField(String name, Object value) throws Exception {
        Field field = GentuReportMojo.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(mojo, value);
    }

}
