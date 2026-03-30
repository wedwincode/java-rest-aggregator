package ru.wedwin.aggregator.adapter.out.codec;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.result.Payload;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JsonCodecTest {

    private final JsonCodec codec = new JsonCodec();

    @Test
    void givenItems_whenWrite_thenProducesJsonArray() {
        StringWriter writer = new StringWriter();
        codec.write(List.of(item(1)), writer);
        String json = writer.toString();

        assertTrue(json.contains("itemId"));
        assertTrue(json.contains("apiId"));
        assertTrue(json.contains("fetchedAt"));
        assertTrue(json.contains("payload"));
    }

    @Test
    void givenJson_whenRead_thenParsesItems() {
        AggregatedItem original = item(1);
        StringWriter writer = new StringWriter();
        codec.write(List.of(original), writer);
        List<AggregatedItem> result = codec.read(new StringReader(writer.toString()));

        assertEquals(1, result.size());

        AggregatedItem parsed = result.getFirst();

        assertEquals(original.itemId(), parsed.itemId());
        assertEquals(original.apiId(), parsed.apiId());
        assertEquals(original.fetchedAt(), parsed.fetchedAt());
        assertEquals(
                ((Payload.PInt) original.payload()).value(),
                ((Payload.PInt) parsed.payload()).value()
        );
    }

    @Test
    void givenMultipleItems_whenWriteAndRead_thenRoundTripWorks() {
        List<AggregatedItem> items = List.of(item(1), item(2));
        StringWriter writer = new StringWriter();
        codec.write(items, writer);
        List<AggregatedItem> result = codec.read(new StringReader(writer.toString()));

        assertEquals(2, result.size());
        assertEquals(1, ((Payload.PInt) result.get(0).payload()).value());
        assertEquals(2, ((Payload.PInt) result.get(1).payload()).value());
    }

    @Test
    void givenNonArrayJson_whenRead_thenThrows() {
        String json = "{\"a\":1}";
        assertThrows(IllegalStateException.class,() -> codec.read(new StringReader(json)));
    }

    @Test
    void givenMissingField_whenRead_thenThrows() {
        String json = """
                [
                  {
                    "apiId": "api",
                    "fetchedAt": "2024-01-01T00:00:00Z",
                    "payload": 1
                  }
                ]
                """;

        assertThrows(IllegalStateException.class, () -> codec.read(new StringReader(json)));
    }

    @Test
    void givenNullElements_whenRead_thenSkipsThem() {
        String json = """
                [
                  null,
                  {
                    "itemId": "00000000-0000-0000-0000-000000000001",
                    "apiId": "api",
                    "fetchedAt": "2024-01-01T00:00:00Z",
                    "payload": 1
                  }
                ]
                """;
        List<AggregatedItem> result = codec.read(new StringReader(json));

        assertEquals(1, result.size());
    }

    private static AggregatedItem item(int value) {
        return new AggregatedItem(
                UUID.randomUUID(),
                new ApiId("api"),
                Instant.parse("2024-01-01T00:00:00Z"),
                new Payload.PInt(value)
        );
    }
}