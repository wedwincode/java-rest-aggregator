package ru.wedwin.aggregator.domain.model.api;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.exception.InvalidApiIdException;

import static org.junit.jupiter.api.Assertions.*;

class ApiIdTest {

    @Test
    void normalizesValue() {
        ApiId id = new ApiId("  API1  ");
        assertEquals("api1", id.value());
        assertEquals("api1", id.toString());
    }

    @Test
    void rejectsNull() {
        assertThrows(InvalidApiIdException.class, () -> new ApiId(null));
    }

    @Test
    void rejectsBlank() {
        assertThrows(InvalidApiIdException.class, () -> new ApiId("   "));
    }
}
