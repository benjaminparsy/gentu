package com.benjamin.parsy.gentu.maven.testresearcher.surefire;

import com.benjamin.parsy.gentu.maven.testresearcher.ExecutedTestCase;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SurefireResearcherTest {

    @Test
    void search_withPassingTests_shouldReturnAll(@TempDir Path tempDir) throws Exception {

        // Given
        SurefireResearcher parser = new SurefireResearcher(tempDir, new SystemStreamLog());
        writeXml(tempDir, "TEST-Foo.xml", """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="2" failures="0" errors="0" skipped="0">
                  <testcase name="firstTest" classname="com.example.FooTest" time="0.01"/>
                  <testcase name="secondTest" classname="com.example.FooTest" time="0.02"/>
                </testsuite>
                """);

        // When
        List<ExecutedTestCase> result = parser.search();

        // Then
        assertEquals(2, result.size());
        assertEquals("com.example.FooTest", result.get(0).getClassName());
        assertEquals("firstTest", result.get(0).getMethodName());
        assertEquals("secondTest", result.get(1).getMethodName());
    }

    @Test
    void search_withMixedResults_shouldReturnAll(@TempDir Path tempDir) throws Exception {

        // Given
        SurefireResearcher parser = new SurefireResearcher(tempDir, new SystemStreamLog());
        writeXml(tempDir, "TEST-Foo.xml", """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="3" failures="1" errors="0" skipped="1">
                  <testcase name="passingTest" classname="com.example.FooTest" time="0.01"/>
                  <testcase name="failingTest" classname="com.example.FooTest" time="0.01">
                    <failure message="fail" type="AssertionError">trace</failure>
                  </testcase>
                  <testcase name="skippedTest" classname="com.example.FooTest" time="0.00">
                    <skipped/>
                  </testcase>
                </testsuite>
                """);

        // When
        List<ExecutedTestCase> result = parser.search();

        // Then
        assertEquals(3, result.size());
    }

    @Test
    void search_withMethodSignature_shouldStripParameters(@TempDir Path tempDir) throws Exception {

        // Given
        SurefireResearcher parser = new SurefireResearcher(tempDir, new SystemStreamLog());
        writeXml(tempDir, "TEST-Foo.xml", """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="1" failures="0" errors="0" skipped="0">
                  <testcase name="paramTest(String, int)" classname="com.example.FooTest" time="0.01"/>
                </testsuite>
                """);

        // When
        List<ExecutedTestCase> result = parser.search();

        // Then
        assertEquals(1, result.size());
        assertEquals("paramTest", result.getFirst().getMethodName());
    }

    @Test
    void search_withNonExistentDirectory_shouldReturnEmpty() throws Exception {

        // Given
        SurefireResearcher parser = new SurefireResearcher(
                Path.of("non-existent-" + System.nanoTime()), new SystemStreamLog());

        // When
        List<ExecutedTestCase> result = parser.search();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void search_withEmptyDirectory_shouldReturnEmpty(@TempDir Path tempDir) throws Exception {

        // Given
        SurefireResearcher parser = new SurefireResearcher(tempDir, new SystemStreamLog());

        // When
        List<ExecutedTestCase> result = parser.search();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void search_withNonXmlFiles_shouldIgnoreThem(@TempDir Path tempDir) throws Exception {

        // Given
        SurefireResearcher parser = new SurefireResearcher(tempDir, new SystemStreamLog());
        Files.writeString(tempDir.resolve("not-a-test.txt"), "some content");
        Files.writeString(tempDir.resolve("REPORT-something.xml"), "<report/>");
        writeXml(tempDir, "TEST-Valid.xml", """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="1" failures="0" errors="0" skipped="0">
                  <testcase name="validTest" classname="com.example.Test" time="0.01"/>
                </testsuite>
                """);

        // When
        List<ExecutedTestCase> result = parser.search();

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void search_withMultipleFiles_shouldAggregateAllResults(@TempDir Path tempDir) throws Exception {

        // Given
        SurefireResearcher parser = new SurefireResearcher(tempDir, new SystemStreamLog());
        writeXml(tempDir, "TEST-First.xml", """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="2" failures="0" errors="0" skipped="0">
                  <testcase name="test1" classname="com.example.FirstTest" time="0.01"/>
                  <testcase name="test2" classname="com.example.FirstTest" time="0.01"/>
                </testsuite>
                """);
        writeXml(tempDir, "TEST-Second.xml", """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="2" failures="1" errors="0" skipped="0">
                  <testcase name="test3" classname="com.example.SecondTest" time="0.01"/>
                  <testcase name="test4" classname="com.example.SecondTest" time="0.01">
                    <failure message="fail" type="AssertionError">trace</failure>
                  </testcase>
                </testsuite>
                """);

        // When
        List<ExecutedTestCase> result = parser.search();

        // Then
        assertEquals(4, result.size());
    }

    private void writeXml(Path dir, String filename, String content) throws IOException {
        Files.writeString(dir.resolve(filename), content);
    }

}
