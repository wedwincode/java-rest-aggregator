package ru.wedwin.aggregator.domain.api;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.exception.InvalidApiIdException;

import static org.junit.jupiter.api.Assertions.*;

class ApiIdTest {

    @Test
    void givenNonNormalizedValue_whenCreateApiId_thenValueIsNormalized() {
        ApiId id = new ApiId("  API1  ");
        assertEquals("api1", id.value());
        assertEquals("api1", id.toString());
    }

    @Test
    void givenNullValue_whenCreateApiId_thenThrowsInvalidApiIdException() {
        assertThrows(InvalidApiIdException.class, () -> new ApiId(null));
    }

    @Test
    void givenBlankValue_whenCreateApiId_thenThrowsInvalidApiIdException() {
        assertThrows(InvalidApiIdException.class, () -> new ApiId("   "));
    }
}
