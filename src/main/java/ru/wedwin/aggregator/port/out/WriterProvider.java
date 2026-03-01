package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.out.WriterId;

public interface WriterProvider {
    Writer getWriter(WriterId id);
}
