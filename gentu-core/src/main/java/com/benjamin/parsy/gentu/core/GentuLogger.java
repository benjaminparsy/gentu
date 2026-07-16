package com.benjamin.parsy.gentu.core;

/**
 * Minimal logging abstraction used by core components, allowing callers to inject
 * their own logging implementation (e.g. a Maven {@code Log} adapter).
 */
public interface GentuLogger {

    /**
     * Returns a no-op implementation, useful for tests.
     */
    static GentuLogger noOp() {
        return new GentuLogger() {
            public void info(String m) {
            }

            public void warn(String m) {
            }

            public void error(String m) {
            }

            public void error(String m, Throwable t) {
            }
        };
    }

    void info(String message);

    void warn(String message);

    void error(String message);

    void error(String message, Throwable cause);

}
