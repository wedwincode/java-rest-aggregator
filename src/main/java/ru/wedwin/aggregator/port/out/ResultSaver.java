package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;

import java.util.List;

public interface ResultSaver {
    void save(OutputSpec spec, List<AggregatedItem> items);
}
