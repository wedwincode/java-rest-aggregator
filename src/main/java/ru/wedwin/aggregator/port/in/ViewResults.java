package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.domain.model.result.exception.ResultViewException;

public interface ViewResults {
    void view(RunConfig runConfig) throws ResultViewException;
}
