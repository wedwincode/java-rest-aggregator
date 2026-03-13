package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.AggregationHandle;
import ru.wedwin.aggregator.domain.model.config.RunConfig;

public interface StartAggregation {
    AggregationHandle start(RunConfig runConfig);
}
