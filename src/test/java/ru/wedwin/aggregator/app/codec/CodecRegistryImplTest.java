package ru.wedwin.aggregator.app.codec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.app.port.out.Codec;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.result.AggregatedItem;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CodecRegistryImplTest {

    private CodecRegistry registry;
    private CodecId id;

    @BeforeEach
    void setUp() {
        registry = new CodecRegistryImpl();
        id = new CodecId("dummy");
    }

    class DummyCodec implements Codec {

        @Override
        public CodecId id() {
            return id;
        }

        @Override
        public List<AggregatedItem> read(Reader r) {
            return List.of();
        }

        @Override
        public void write(List<AggregatedItem> items, Writer w) {
        }
    }


    @Test
    void givenRegisteredCodec_whenGet_thenReturnsCodec() {
        Codec codec = new DummyCodec();
        registry.put(codec);

        assertEquals(codec, registry.get(id));
    }

    @Test
    void givenUnknownId_whenGet_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> registry.get(new CodecId("unknown")));
    }

    @Test
    void givenRegisteredCodec_whenList_thenReturnsCodecIds() {
        Codec codec = new DummyCodec();
        registry.put(codec);

        assertEquals(List.of(codec.id()), registry.list());
    }
}
