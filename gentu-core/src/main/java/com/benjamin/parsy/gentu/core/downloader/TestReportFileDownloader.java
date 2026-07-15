package com.benjamin.parsy.gentu.core.downloader;

import com.benjamin.parsy.gentu.core.dto.TestResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface TestReportFileDownloader {

    void downloadFile(List<TestResult> results, Path directory) throws IOException;

}
