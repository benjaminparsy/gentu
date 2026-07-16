package com.benjamin.parsy.gentu.maven.testresearcher.surefire;

import com.benjamin.parsy.gentu.maven.testresearcher.ExecutedTestCase;

/**
 * {@link ExecutedTestCase} populated from a Surefire XML {@code <testcase>} element,
 * carrying the raw execution time in addition to class and method name.
 */
public class SurefireExecutedTestCase extends ExecutedTestCase {

    private final String time;

    public SurefireExecutedTestCase(String className, String methodName, String time) {
        super(className, methodName);
        this.time = time;
    }

    /**
     * Raw execution time as reported by Surefire (e.g. {@code "0.042"}).
     */
    public String getTime() {
        return time;
    }

}
