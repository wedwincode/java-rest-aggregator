package ru.wedwin.aggregator.domain.config;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.config.exception.InvalidOutputSpecException;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OutputSpecTest {

    private static final Path PATH = Path.of("/");
    private static final CodecId CODEC_ID = new CodecId("codec1");
    private static final WriteMode MODE = WriteMode.NEW;

    @Test
    void givenValidValues_whenCreateOutputSpec_thenFieldsAreSet() {
        OutputSpec spec = new OutputSpec(PATH, CODEC_ID, MODE);

        assertEquals(PATH, spec.path());
        assertEquals(CODEC_ID, spec.codecId());
        assertEquals(MODE, spec.mode());
    }

    @Test
    void givenNullPath_whenCreateOutputSpec_thenThrowsException() {
        assertThrows(InvalidOutputSpecException.class,
                () -> new OutputSpec(null, CODEC_ID, MODE));
    }

    @Test
    void givenNullCodecId_whenCreateOutputSpec_thenThrowsException() {
        assertThrows(InvalidOutputSpecException.class,
                () -> new OutputSpec(PATH, null, MODE));
    }

    @Test
    void givenNullMode_whenCreateOutputSpec_thenThrowsException() {
        assertThrows(InvalidOutputSpecException.class,
                () -> new OutputSpec(PATH, CODEC_ID, null));
    }

}
