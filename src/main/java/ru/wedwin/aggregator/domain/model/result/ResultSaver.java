package ru.wedwin.aggregator.domain.model.result;

import ru.wedwin.aggregator.domain.model.output.OutputSpec;

import java.util.List;

public interface ResultSaver {
    void save(OutputSpec spec, List<AggregatedItem> items);
}
