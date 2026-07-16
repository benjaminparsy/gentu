package com.benjamin.parsy.gentu.core.downloader;

import com.benjamin.parsy.gentu.core.dto.TestResult;

import java.nio.file.Path;
import java.util.List;

/**
 * Copies files referenced in {@link TestResult} instances into a local output directory.
 */
public interface TestReportFileDownloader {

    /**
     * Copies each result's absolute file paths into {@code baseDirectory}.
     * Results without files are skipped. Implementations may handle individual
     * copy failures internally rather than propagating them.
     *
     * @param results       the test results whose files should be copied
     * @param baseDirectory root output directory; one subdirectory per result may be created
     * @throws DownloaderException if an error occurs while downloading the files (e.g., cannot create output directories)
     */
    void downloadFile(List<TestResult> results, Path baseDirectory) throws DownloaderException;

}
