package com.benjamin.parsy.gentu.maven.testresearcher;

/**
 * Thrown when a {@link TestResearcher} cannot read or parse its source.
 */
public class TestResearcherException extends Exception {

    public TestResearcherException(String message, Throwable cause) {
        super(message, cause);
    }

}
