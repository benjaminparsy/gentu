package com.benjamin.parsy.gentu.core.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TestResult(
        int id,
        String testName,
        String description,
        List<String> givenValue,
        List<GivenFile> givenFiles,
        List<String> expected,
        String className,
        String methodName,
        LocalDateTime executedAt
) {

}