package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.output.WriterId;

public interface WriterProvider {
    Writer getWriter(WriterId id);
}
