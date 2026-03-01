package ru.wedwin.aggregator.adapter.out.formatters;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.output.FormatterId;
import ru.wedwin.aggregator.port.out.Formatter;

import java.util.List;

public class ConsoleFormatter implements Formatter {
    @Override
    public FormatterId id() {
        return new FormatterId("console");
    }

    @Override
    public void format(List<AggregatedItem> items, OutputSpec spec) {
        for (AggregatedItem item: items) {
            System.out.println(item);
        }
    }
}
