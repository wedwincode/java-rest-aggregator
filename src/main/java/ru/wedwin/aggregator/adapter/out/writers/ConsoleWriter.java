package ru.wedwin.aggregator.adapter.out.writers;

import ru.wedwin.aggregator.domain.model.AggregatedItem;
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
    public void write(List<AggregatedItem> items, OutputSpec spec) {
        for (AggregatedItem item: items) {
            System.out.println(item);
        }
    }
}
