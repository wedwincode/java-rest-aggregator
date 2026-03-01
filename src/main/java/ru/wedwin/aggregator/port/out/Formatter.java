package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.format.FormatterId;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public interface Formatter {
    FormatterId id();
    List<AggregatedItem> read(Reader r) throws IOException;
    void write(List<AggregatedItem> items, Writer w) throws IOException;
}
