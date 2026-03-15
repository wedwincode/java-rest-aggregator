package ru.wedwin.aggregator.app.port.out;

import ru.wedwin.aggregator.domain.config.OutputSpec;
import ru.wedwin.aggregator.domain.result.AggregatedItem;

import java.util.List;

public interface ResultSaver {
    void save(OutputSpec spec, List<AggregatedItem> items);
}
