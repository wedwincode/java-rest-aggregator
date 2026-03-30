package ru.wedwin.aggregator.domain.config;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.config.exception.InvalidDisplaySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DisplaySpecTest {

    @Test
    void givenValidApiIdAndMode_whenCreateDisplaySpec_thenFieldsAreSet() {
        ApiId id = new ApiId("api1");
        DisplayMode mode = DisplayMode.ALL;

        DisplaySpec spec = new DisplaySpec(id, mode);

        assertEquals(id, spec.apiId());
        assertEquals(mode, spec.mode());
    }

    @Test
    void givenValidMode_whenCreateDisplaySpecWithoutApiId_thenApiIdIsNull() {
        DisplayMode mode = DisplayMode.ALL;

        DisplaySpec spec = new DisplaySpec(mode);

        assertNull(spec.apiId());
        assertEquals(mode, spec.mode());
    }

    @Test
    void givenNullMode_whenCreateDisplaySpec_thenThrowsException() {
        assertThrows(InvalidDisplaySpecException.class, () -> new DisplaySpec(null));
    }
}
