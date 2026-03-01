package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.AggregatedItem;
import ru.wedwin.aggregator.domain.model.out.OutputSpec;
import ru.wedwin.aggregator.domain.model.out.WriterId;

import java.util.List;

public interface Writer {
    WriterId id();
    void write(List<AggregatedItem> records, OutputSpec spec);
}
