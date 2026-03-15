package ru.wedwin.aggregator.app.port.in;

import ru.wedwin.aggregator.domain.config.RunConfig;

public interface ViewResultsUseCase {
    void view(RunConfig runConfig);
}
