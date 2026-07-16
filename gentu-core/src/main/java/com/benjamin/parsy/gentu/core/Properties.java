package com.benjamin.parsy.gentu.core;

/**
 * Shared constants used across the Gentu framework.
 */
public final class Properties {

    /**
     * Name of the directory created inside the output directory to hold Gentu artifacts.
     */
    public static final String GENTU_DIRECTORY_NAME = "gentu";

    /**
     * Prefix of the generated report file name (followed by a timestamp).
     */
    public static final String TEST_REPORT_BASE_FILENAME = "test-report_";

    private Properties() {
        throw new UnsupportedOperationException("Properties is a utility class and cannot be instantiated");
    }

}
