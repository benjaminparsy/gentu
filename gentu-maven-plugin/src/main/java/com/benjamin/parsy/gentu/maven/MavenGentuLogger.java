package com.benjamin.parsy.gentu.maven;

import com.benjamin.parsy.gentu.core.GentuLogger;
import org.apache.maven.plugin.logging.Log;

/**
 * Adapts Maven's {@link Log} to the {@link GentuLogger} interface used by core components.
 */
public class MavenGentuLogger implements GentuLogger {

    private final Log log;

    public MavenGentuLogger(Log log) {
        this.log = log;
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public void error(String message, Throwable cause) {
        log.error(message, cause);
    }

}
