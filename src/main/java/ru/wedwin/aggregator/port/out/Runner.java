package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.app.session.Session;
import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.result.AggregatedItem;

import java.util.function.Consumer;

public interface Runner {
    Session start(RunConfig config, Consumer<AggregatedItem> onResult, Consumer<Throwable> onError);
    void stop(Session session);
}
