package com.benjamin.parsy.gentu.maven.testresearcher;

import java.util.List;

/**
 * Strategy for discovering executed test cases from a test execution report.
 */
public interface TestResearcher {

    /**
     * Searches for executed test cases in the configured source (e.g. Surefire reports directory).
     *
     * @return list of executed test cases; never {@code null}
     * @throws TestResearcherException if the source cannot be read
     */
    List<ExecutedTestCase> search() throws TestResearcherException;

}
