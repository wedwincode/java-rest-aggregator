package ru.wedwin.aggregator.domain.model.result;

import ru.wedwin.aggregator.domain.model.api.ApiId;

import java.time.Instant;
import java.util.UUID;

public record AggregatedItem(
        UUID itemId, // collision probability: 1 / 2.71*10^18
        ApiId apiId,
        Instant fetchedAt,
        Payload payload
) {
    public AggregatedItem(ApiId apiId, Payload payload) {
        this(UUID.randomUUID(), apiId, Instant.now(), payload);
    }
}
