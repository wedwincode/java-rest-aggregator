package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.format.FormatterId;

import java.util.List;

public interface Formatter {
    FormatterId id();
    void format(List<AggregatedItem> items, OutputSpec spec);
}
