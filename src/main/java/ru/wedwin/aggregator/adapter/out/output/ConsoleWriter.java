package ru.wedwin.aggregator.adapter.out.output;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.out.OutputSpec;
import ru.wedwin.aggregator.port.out.OutputWriter;

import java.util.List;

public class ConsoleWriter implements OutputWriter {
    @Override
    public String id() {
        return "console";
    }

    @Override
    public void write(List<AggregatedRecord> records, OutputSpec spec) {
        for (AggregatedRecord rec: records) {
            System.out.println(rec);
        }
    }
}
