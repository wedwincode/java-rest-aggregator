package ru.wedwin.aggregator.adapter.out.writers;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.out.OutputSpec;
import ru.wedwin.aggregator.domain.model.out.WriterId;
import ru.wedwin.aggregator.port.out.Writer;

import java.util.List;

public class ConsoleWriter implements Writer {
    @Override
    public WriterId id() {
        return new WriterId("console");
    }

    @Override
    public void write(List<AggregatedRecord> records, OutputSpec spec) {
        for (AggregatedRecord rec: records) {
            System.out.println(rec);
        }
    }
}
