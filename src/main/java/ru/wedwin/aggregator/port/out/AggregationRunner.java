package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.app.service.session.Session;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;

import java.util.function.Consumer;

public interface AggregationRunner {
    Session start(RunConfig config, Consumer<AggregatedItem> onResult, Consumer<Throwable> onError);
    void stop(Session handle);
}
