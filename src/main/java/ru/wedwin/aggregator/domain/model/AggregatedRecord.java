package ru.wedwin.aggregator.domain.model;

import java.time.Instant;
import java.util.Map;

public record AggregatedRecord (
        // todo incremental
        String itemId,
        ApiId apiId,
        Instant timestamp,
        Map<String, Object> fields,
        Payload payload
) {}
