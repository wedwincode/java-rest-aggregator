package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.app.service.session.Session;
import ru.wedwin.aggregator.domain.model.config.RunConfig;

public interface StartAggregation {
    Session start(RunConfig runConfig);
}
