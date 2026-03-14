package ru.wedwin.aggregator.domain.model.api;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.exception.InvalidApiIdException;

import static org.junit.jupiter.api.Assertions.*;

class ApiIdTest {

    @Test
    void normalizes_value() {
        ApiId id = new ApiId("  API1  ");
        assertEquals("api1", id.value());
        assertEquals("api1", id.toString());
    }

    @Test
    void rejects_null() {
        assertThrows(InvalidApiIdException.class, () -> new ApiId(null));
    }

    @Test
    void rejects_blank() {
        assertThrows(InvalidApiIdException.class, () -> new ApiId("   "));
    }
}
