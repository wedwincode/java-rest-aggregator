package ru.wedwin.aggregator.domain.model.codec;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.codec.exception.InvalidCodecIdException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CodecIdTest {
    @Test
    void normalizesValue() {
        CodecId id = new CodecId("  CODEC1  ");
        assertEquals("codec1", id.value());
        assertEquals("codec1", id.toString());
    }

    @Test
    void rejectsNull() {
        assertThrows(InvalidCodecIdException.class, () -> new CodecId(null));
    }

    @Test
    void rejectsBlank() {
        assertThrows(InvalidCodecIdException.class, () -> new CodecId("   "));
    }

    @Test
    void returnsToString() {
        CodecId codec = new CodecId("codec");
        assertEquals("codec", codec.toString());
    }
}
