package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.AggregationHandle;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;

import java.util.function.Consumer;

public interface AggregationRunner {
    AggregationHandle start(RunConfig config, Consumer<AggregatedItem> onResult, Consumer<Throwable> onError);
    void stop(AggregationHandle handle);
}
