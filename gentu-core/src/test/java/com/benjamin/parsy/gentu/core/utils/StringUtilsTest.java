package com.benjamin.parsy.gentu.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {

    @Test
    void constructor_shouldThrowException() throws Exception {

        // Given
        Constructor<StringUtils> constructor = StringUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // When / Then
        assertThrows(Exception.class, constructor::newInstance);
    }

    @Test
    void isEmpty_withNull_shouldReturnTrue() {
        assertTrue(StringUtils.isEmpty(null));
    }

    @Test
    void isEmpty_withEmptyString_shouldReturnTrue() {
        assertTrue(StringUtils.isEmpty(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  ", "\t", "\n", " \t\n "})
    void isEmpty_withBlankString_shouldReturnTrue(String value) {
        assertTrue(StringUtils.isEmpty(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "hello", " hello ", "  x  "})
    void isEmpty_withNonBlankString_shouldReturnFalse(String value) {
        assertFalse(StringUtils.isEmpty(value));
    }

    @Test
    void isNotEmpty_withNull_shouldReturnFalse() {
        assertFalse(StringUtils.isNotEmpty(null));
    }

    @Test
    void isNotEmpty_withEmptyString_shouldReturnFalse() {
        assertFalse(StringUtils.isNotEmpty(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    void isNotEmpty_withBlankString_shouldReturnFalse(String value) {
        assertFalse(StringUtils.isNotEmpty(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "hello", " hello ", "  x  "})
    void isNotEmpty_withNonBlankString_shouldReturnTrue(String value) {
        assertTrue(StringUtils.isNotEmpty(value));
    }

}
