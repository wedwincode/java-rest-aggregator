package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.AggregationHandle;

public interface StopAggregation {
    void stop(AggregationHandle handle);
}
