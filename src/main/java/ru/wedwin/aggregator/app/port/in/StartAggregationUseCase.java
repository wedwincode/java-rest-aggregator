package ru.wedwin.aggregator.app.port.in;

import ru.wedwin.aggregator.app.session.Session;
import ru.wedwin.aggregator.domain.config.RunConfig;

public interface StartAggregationUseCase {
    Session start(RunConfig runConfig);
}
