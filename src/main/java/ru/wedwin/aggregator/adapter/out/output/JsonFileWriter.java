package ru.wedwin.aggregator.adapter.out.output;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.OutputSpec;
import ru.wedwin.aggregator.port.out.OutputWriter;

import java.util.List;

public class JsonFileWriter implements OutputWriter {

    @Override
    public String id() {
        return "json";
    }

    @Override
    public void write(List<AggregatedRecord> records, OutputSpec spec) {

    }
}
