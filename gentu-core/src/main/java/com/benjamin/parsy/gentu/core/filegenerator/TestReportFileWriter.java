package com.benjamin.parsy.gentu.core.filegenerator;

import com.benjamin.parsy.gentu.core.dto.TestResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface TestReportFileWriter {

    void writeReport(List<TestResult> results, Path directory) throws IOException;

}
