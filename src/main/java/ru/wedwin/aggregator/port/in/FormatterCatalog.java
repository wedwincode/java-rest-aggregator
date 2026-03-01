package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.format.FormatterId;

import java.util.List;

public interface FormatterCatalog {
    List<FormatterId> list();
}
