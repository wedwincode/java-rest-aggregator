package ru.wedwin.aggregator.app.registry;

import ru.wedwin.aggregator.domain.model.output.FormatterId;
import ru.wedwin.aggregator.port.in.FormatterCatalog;
import ru.wedwin.aggregator.port.out.Formatter;
import ru.wedwin.aggregator.port.out.FormatterProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormatterRegistry implements FormatterCatalog, FormatterProvider {
    private final Map<FormatterId, Formatter> byId;

    public FormatterRegistry(List<Formatter> formatters) {
        byId = formatters.stream().collect(Collectors.toMap(Formatter::id, c -> c));
    }

    public List<FormatterId> list() {
        return new ArrayList<>(byId.keySet());
    }

    public Formatter getFormatter(FormatterId id) {
        Formatter formatter = byId.get(id);
        if (formatter == null) {
            throw new IllegalArgumentException("unknown format: " + id);
        }
        return formatter;
    }

}
