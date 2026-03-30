package ru.wedwin.aggregator.domain.codec;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.codec.exception.InvalidCodecIdException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CodecIdTest {

    @Test
    void givenNonNormalizedValue_whenCreateCodecId_thenValueIsNormalized() {
        CodecId id = new CodecId("  CODEc1  ");
        assertEquals("codec1", id.value());
        assertEquals("codec1", id.toString());
    }

    @Test
    void givenNullValue_whenCreateCodecId_thenThrowsInvalidCodecIdException() {
        assertThrows(InvalidCodecIdException.class, () -> new CodecId(null));
    }

    @Test
    void givenBlankValue_whenCreateCodecId_thenThrowsInvalidCodecIdException() {
        assertThrows(InvalidCodecIdException.class, () -> new CodecId("   "));
    }

    @Test
    void givenValue_whenCallToString_thenReturnsValue() {
        CodecId codec = new CodecId("codec");
        assertEquals("codec", codec.toString());
    }
}
