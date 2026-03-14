package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.config.RunConfig;
import ru.wedwin.aggregator.domain.result.exception.ResultViewException;

public interface ViewResultsUseCase {
    void view(RunConfig runConfig) throws ResultViewException;
}
