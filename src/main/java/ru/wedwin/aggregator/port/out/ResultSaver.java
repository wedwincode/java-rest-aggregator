package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.output.OutputSpec;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.result.exception.ResultSaveException;

import java.util.List;

public interface ResultSaver {
    void save(OutputSpec spec, List<AggregatedItem> items) throws ResultSaveException;
}
