package ru.wedwin.aggregator.domain.model.result;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.result.exception.InvalidAggregatedItemException;

import java.time.Instant;
import java.util.UUID;

public record AggregatedItem(
        UUID itemId, // collision probability: 1 / 2.71*10^18
        ApiId apiId,
        Instant fetchedAt,
        Payload payload
) {
    public AggregatedItem {
        if (itemId == null) {
            throw new InvalidAggregatedItemException("itemId is null");
        }
        if (apiId == null) {
            throw new InvalidAggregatedItemException("apiId is null");
        }
        if (fetchedAt == null) {
            throw new InvalidAggregatedItemException("fetchedAt is null");
        }
        if (payload == null) {
            throw new InvalidAggregatedItemException("payload is null");
        }
    }

    public AggregatedItem(ApiId apiId, Payload payload) {
        this(UUID.randomUUID(), apiId, Instant.now(), payload);
    }
}
