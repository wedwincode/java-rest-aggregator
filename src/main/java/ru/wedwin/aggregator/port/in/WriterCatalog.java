package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.output.WriterId;

import java.util.List;

public interface WriterCatalog {
    List<WriterId> list();
}
