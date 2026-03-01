package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.output.FormatterId;

public interface FormatterProvider {
    Formatter getFormatter(FormatterId id);
}
