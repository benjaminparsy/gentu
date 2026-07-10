package com.benjamin.parsy.gentu.core.downloader;

import com.benjamin.parsy.gentu.core.dto.GivenFile;
import com.benjamin.parsy.gentu.core.dto.TestResult;
import com.benjamin.parsy.gentu.core.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class TestReportFileDownloaderImpl implements TestReportFileDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(TestReportFileDownloaderImpl.class);

    @Override
    public void downloadFile(List<TestResult> results, Path directory) {
        results.forEach(result -> handleTestResult(directory, result));
    }

    private void handleTestResult(Path baseDirectory, TestResult result) {

        Path targetDirectory = baseDirectory.resolve("test_" + result.id());

        try {
            FileUtils.forceCreateDirectory(targetDirectory);
        } catch (IOException e) {
            LOG.error("Unable to create directory {}", targetDirectory);
            return;
        }

        if (result.givenFiles() != null && !result.givenFiles().isEmpty()) {
            List<GivenFile> givenFiles = result.givenFiles();
            givenFiles.forEach(file -> handleFile(file, targetDirectory));
        }

    }

    private void handleFile(GivenFile file, Path targetDirectory) {
        LOG.debug("File processing {}", file.rawPath());
        if (file.isInClasspath()) {
            downloadFromClasspath(file.rawPath(), targetDirectory);
        } else {
            downloadFromAbsolutePath(file.rawPath(), targetDirectory);
        }
    }

    private void downloadFromClasspath(String rawPath, Path directory) {

        String filename = extractFilename(rawPath);
        if (filename.isBlank()) {
            LOG.warn("Unable to determine the filename for the file {}", rawPath);
            return;
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(rawPath)) {
            if (is == null) {
                LOG.warn("The file {} does not exist in the classpath", rawPath);
                return;
            }
            LOG.debug("Download the file from the classpath {}", rawPath);
            Files.copy(is, directory.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOG.error("An error occurred while copying the file {}", rawPath, e);
        }
    }

    private void downloadFromAbsolutePath(String rawPath, Path directory) {

        try {
            Path absolutePath = Path.of(rawPath);
            LOG.debug("Download the file from the absolute path {}", absolutePath);
            Files.copy(absolutePath, directory.resolve(absolutePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOG.error("An error occurred while copying the file {}", rawPath, e);
        }
    }

    private String extractFilename(String path) {
        int idx = path.lastIndexOf('/');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }

}
