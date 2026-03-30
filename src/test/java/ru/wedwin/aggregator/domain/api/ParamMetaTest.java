package ru.wedwin.aggregator.domain.api;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.exception.InvalidParamMetaException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParamMetaTest {

    static final String KEY = "key";
    static final String DEFAULT_VALUE = "default";
    static final String DESCRIPTION = "desc";

    @Test
    void givenKeyWithSpacesAndUppercase_whenCreateParamMeta_thenKeyIsNormalized() {
        ParamMeta meta = new ParamMeta("  Param1  ", true, DEFAULT_VALUE, DESCRIPTION);

        assertEquals("param1", meta.key());
        assertTrue(meta.required());
        assertEquals(DEFAULT_VALUE, meta.defaultValue());
        assertEquals(DESCRIPTION, meta.description());
    }

    @Test
    void givenNullKey_whenCreateParamMeta_thenThrowsException() {
        assertThrows(InvalidParamMetaException.class,
                () -> new ParamMeta(null, true, DEFAULT_VALUE, DESCRIPTION));
    }

    @Test
    void givenBlankKey_whenCreateParamMeta_thenThrowsException() {
        assertThrows(InvalidParamMetaException.class,
                () -> new ParamMeta("   ", true, DEFAULT_VALUE, DESCRIPTION));
    }

    @Test
    void givenValidParamMeta_whenCallToString_thenReturnsFormattedString() {
        ParamMeta meta = new ParamMeta(KEY, true, DEFAULT_VALUE, DESCRIPTION);
        String expected = "key=key, required=true, defaultValue=default, description=desc";

        assertEquals(expected, meta.toString());
    }
}
