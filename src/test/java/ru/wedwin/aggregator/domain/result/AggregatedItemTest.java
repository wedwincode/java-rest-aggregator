package ru.wedwin.aggregator.domain.result;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.result.exception.InvalidAggregatedItemException;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AggregatedItemTest {

    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final ApiId API_ID = new ApiId("api1");
    private static final Instant FETCHED_AT = Instant.now();
    private static final Payload PAYLOAD = new Payload.PInt(10);

    @Test
    void givenValidValues_whenCreateAggregatedItem_thenFieldsAreSet() {
        AggregatedItem item1 = new AggregatedItem(ITEM_ID, API_ID, FETCHED_AT, PAYLOAD);

        assertEquals(ITEM_ID, item1.itemId());
        assertEquals(API_ID, item1.apiId());
        assertEquals(FETCHED_AT, item1.fetchedAt());
        assertEquals(PAYLOAD, item1.payload());
    }

    @Test
    void givenValidApiIdAndPayload_whenCreateAggregatedItem_thenGeneratedFieldsAreSet() {
        AggregatedItem item = new AggregatedItem(API_ID, PAYLOAD);

        assertNotNull(item.itemId());
        assertEquals(API_ID, item.apiId());
        assertNotNull(item.fetchedAt());
        assertEquals(PAYLOAD, item.payload());
    }

    @Test
    void givenNullItemId_whenCreateAggregatedItem_thenThrowsException() {
        assertThrows(InvalidAggregatedItemException.class,
                () -> new AggregatedItem(null, API_ID, FETCHED_AT, PAYLOAD));
    }

    @Test
    void givenNullApiId_whenCreateAggregatedItem_thenThrowsException() {
        assertThrows(InvalidAggregatedItemException.class,
                () -> new AggregatedItem(ITEM_ID, null, FETCHED_AT, PAYLOAD));
    }

    @Test
    void givenNullFetchedAt_whenCreateAggregatedItem_thenThrowsException() {
        assertThrows(InvalidAggregatedItemException.class,
                () -> new AggregatedItem(ITEM_ID, API_ID, null, PAYLOAD));
    }

    @Test
    void givenNullPayload_whenCreateAggregatedItem_thenThrowsException() {
        assertThrows(InvalidAggregatedItemException.class,
                () -> new AggregatedItem(ITEM_ID, API_ID, FETCHED_AT, null));
    }
}
