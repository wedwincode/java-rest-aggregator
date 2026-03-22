package ru.wedwin.aggregator.domain.model.api;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.ParamMeta;
import ru.wedwin.aggregator.domain.api.exception.InvalidParamMetaException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParamMetaTest {

    static final String key = "key";
    static final String defaultValue = "default";
    static final String description = "desc";

    @Test
    void normalizesValue() {
        ParamMeta id = new ParamMeta("  Param1  ", true, defaultValue, description);
        assertEquals("param1", id.key());
        assertTrue(id.required());
        assertEquals(defaultValue, id.defaultValue());
        assertEquals(description, id.description());
    }

    @Test
    void rejectsNullKey() {
        assertThrows(InvalidParamMetaException.class, () -> new ParamMeta(null, true, defaultValue, description));
    }

    @Test
    void rejectsBlankKey() {
        assertThrows(InvalidParamMetaException.class, () -> new ParamMeta("   ", true, defaultValue, description));
    }

    @Test
    void returnsToString() {
        ParamMeta meta = new ParamMeta(key, true, defaultValue, description);
        String expected = "key=key, required=true, defaultValue=default, description=desc";
        assertEquals(expected, meta.toString());
    }
}
