package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.output.WriterId;

import java.util.List;

public interface Writer {
    WriterId id();
    void write(List<AggregatedItem> records, OutputSpec spec);
}
