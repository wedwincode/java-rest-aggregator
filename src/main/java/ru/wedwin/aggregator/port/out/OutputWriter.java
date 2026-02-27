package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.out.OutputSpec;

import java.util.List;

public interface OutputWriter {
    String id();
    void write(List<AggregatedRecord> records, OutputSpec spec);
}
