package com.benjamin.parsy.gentu.core.downloader;

import com.benjamin.parsy.gentu.core.GentuLogger;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.core.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Default {@link TestReportFileDownloader} that copies each result's absolute file paths
 * into a per-result subdirectory ({@code test_<id>/}) under the base output directory.
 * Results without files are silently skipped.
 */
public class TestReportFileDownloaderImpl implements TestReportFileDownloader {

    private final GentuLogger log;

    public TestReportFileDownloaderImpl(GentuLogger log) {
        this.log = log;
    }

    @Override
    public void downloadFile(List<TestResult> results, Path baseDirectory) throws DownloaderException {

        for (TestResult result : results) {

            if (result.absoluteFilePaths() == null || result.absoluteFilePaths().isEmpty()) {
                log.info("No files to download for test " + result.testName());
                continue;
            }

            Path targetDirectory = baseDirectory.resolve("test_" + result.id());

            try {
                FileUtils.forceCreateDirectory(targetDirectory);
            } catch (IOException e) {
                throw new DownloaderException("Unable to create directory " + targetDirectory, e);
            }

            for (Path absolutePath : result.absoluteFilePaths()) {
                try {
                    Files.copy(absolutePath, targetDirectory.resolve(absolutePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new DownloaderException("An error occurred while copying the file " + absolutePath, e);
                }
            }

        }
    }

}
