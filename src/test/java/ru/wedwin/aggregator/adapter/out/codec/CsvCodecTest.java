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

class CsvCodecTest {

    private final CsvCodec codec = new CsvCodec();

    @Test
    void givenItems_whenWrite_thenProducesCsv() throws CodecException {
        StringWriter writer = new StringWriter();
        AggregatedItem item = item(1);
        codec.write(List.of(item), writer);
        String csv = writer.toString();

        assertTrue(csv.contains("itemId"));
        assertTrue(csv.contains("apiId"));
        assertTrue(csv.contains("fetchedAt"));
        assertTrue(csv.contains("payload"));
    }

    @Test
    void givenCsv_whenRead_thenParsesItems() throws CodecException {
        AggregatedItem item = item(1);
        StringWriter writer = new StringWriter();
        codec.write(List.of(item), writer);
        String csv = writer.toString();
        List<AggregatedItem> result = codec.read(new StringReader(csv));

        assertEquals(1, result.size());

        AggregatedItem parsed = result.getFirst();

        assertEquals(item.itemId(), parsed.itemId());
        assertEquals(item.apiId(), parsed.apiId());
        assertEquals(item.fetchedAt(), parsed.fetchedAt());
        assertEquals(((Payload.PInt) item.payload()).value(),
                ((Payload.PInt) parsed.payload()).value());
    }

    @Test
    void givenMultipleItems_whenWriteAndRead_thenRoundTripWorks() throws CodecException {
        List<AggregatedItem> items = List.of(
                item(1),
                item(2)
        );
        StringWriter writer = new StringWriter();
        codec.write(items, writer);
        List<AggregatedItem> result = codec.read(new StringReader(writer.toString()));

        assertEquals(2, result.size());
        assertEquals(1, ((Payload.PInt) result.get(0).payload()).value());
        assertEquals(2, ((Payload.PInt) result.get(1).payload()).value());
    }

    @Test
    void givenEmptyCsv_whenRead_thenThrows() {
        assertThrows(IllegalStateException.class, () -> codec.read(new StringReader("")));
    }

    @Test
    void givenMissingRequiredColumn_whenRead_thenThrows() {
        String csv = """
                apiId,fetchedAt
                api,2024-01-01T00:00:00Z
                """;

        assertThrows(IllegalStateException.class,
                () -> codec.read(new StringReader(csv)));
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