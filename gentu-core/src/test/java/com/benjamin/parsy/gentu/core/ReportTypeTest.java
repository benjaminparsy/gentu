package com.benjamin.parsy.gentu.core;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportTypeTest {

    @Test
    void exists_withKnownType_shouldReturnTrue() {

        // When
        boolean result = ReportType.exists("TEXT");

        // Then
        assertTrue(result);
    }

    @Test
    void exists_withKnownTypeLowerCase_shouldReturnTrue() {

        // When
        boolean result = ReportType.exists("text");

        // Then
        assertTrue(result);
    }

    @Test
    void exists_withUnknownType_shouldReturnFalse() {

        // When
        boolean result = ReportType.exists("unknown");

        // Then
        assertFalse(result);
    }

    @Test
    void exists_withNull_shouldReturnFalse() {

        // When
        boolean result = ReportType.exists(null);

        // Then
        assertFalse(result);
    }

    @Test
    void safeValueOf_withKnownType_shouldReturnPresent() {

        // When
        Optional<ReportType> result = ReportType.safeValueOf("TEXT");

        // Then
        assertTrue(result.isPresent());
        assertEquals(ReportType.TEXT, result.get());
    }

    @Test
    void safeValueOf_withKnownTypeLowerCase_shouldReturnPresent() {

        // When
        Optional<ReportType> result = ReportType.safeValueOf("text");

        // Then
        assertTrue(result.isPresent());
        assertEquals(ReportType.TEXT, result.get());
    }

    @Test
    void safeValueOf_withUnknownType_shouldReturnEmpty() {

        // When
        Optional<ReportType> result = ReportType.safeValueOf("unknown");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void safeValueOf_withNull_shouldReturnEmpty() {

        // When
        Optional<ReportType> result = ReportType.safeValueOf(null);

        // Then
        assertTrue(result.isEmpty());
    }

}
