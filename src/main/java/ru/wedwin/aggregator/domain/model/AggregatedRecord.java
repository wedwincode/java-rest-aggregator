package ru.wedwin.aggregator.domain.model;

import ru.wedwin.aggregator.domain.model.api.ApiId;

import java.time.Instant;
import java.util.UUID;

public record AggregatedRecord (
        UUID itemId, // collision probability: 1 / 2.71*10^18
        ApiId apiId,
        Instant timestamp,
        Payload payload
) {
    public AggregatedRecord(ApiId apiId, Payload payload) {
        this(UUID.randomUUID(), apiId, Instant.now(), payload);
    }
}
