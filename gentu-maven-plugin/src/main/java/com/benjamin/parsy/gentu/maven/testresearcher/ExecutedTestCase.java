package com.benjamin.parsy.gentu.maven.testresearcher;

/**
 * Minimal test-case descriptor extracted from a Surefire XML report.
 */
public abstract class ExecutedTestCase {

    private final String className;
    private final String methodName;

    protected ExecutedTestCase(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

}
