package com.benjamin.parsy.gentu.junit5.dto;

import com.benjamin.parsy.gentu.core.dto.TestResult;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestResultRegistry {

    private static final List<TestResult> results = new CopyOnWriteArrayList<>();

    private TestResultRegistry() {
        throw new UnsupportedOperationException("TestResultRegistry is a utility class and cannot be instantiated");
    }

    public static void add(TestResult result) {
        results.add(result);
    }

    public static List<TestResult> getAll() {
        return Collections.unmodifiableList(results);
    }

    public static void clear() {
        results.clear();
    }

    public static int size() {
        return results.size();
    }

}
