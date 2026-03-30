package ru.wedwin.aggregator.adapter.out.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnvReaderTest {

    @Test
    void givenMissingEnv_whenGet_thenReturnsEmptyString() {
        String value = EnvReader.get("THIS_ENV_DOES_NOT_EXIST_123");
        assertEquals("", value);
    }

    @Test
    void givenExistingEnv_whenGet_thenReturnsValue() {
        String expected = System.getenv("PATH");
        if (expected == null) {
            return;
        }
        String actual = EnvReader.get("PATH");

        assertEquals(expected, actual);
    }
}