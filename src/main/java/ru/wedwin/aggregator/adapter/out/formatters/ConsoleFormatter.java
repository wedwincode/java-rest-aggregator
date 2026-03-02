package ru.wedwin.aggregator.adapter.out.formatters;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.format.FormatterId;
import ru.wedwin.aggregator.port.out.Formatter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public class ConsoleFormatter implements Formatter {
    @Override
    public FormatterId id() {
        return new FormatterId("console");
    }

    @Override
    public List<AggregatedItem> read(Reader r) {
        return List.of();
    }

    @Override
    public void write(List<AggregatedItem> items, Writer w) {

    }
}
