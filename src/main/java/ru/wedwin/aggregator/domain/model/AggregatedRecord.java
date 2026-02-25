package ru.wedwin.aggregator.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AggregatedRecord (
        // todo incremental
        UUID itemId,
        ApiId apiId,
        Instant timestamp,
        Payload payload
) {
    public AggregatedRecord {
    }

    public AggregatedRecord(ApiId apiId, Payload payload) {
        this(UUID.randomUUID(), apiId, Instant.now(), payload);
    }
}
