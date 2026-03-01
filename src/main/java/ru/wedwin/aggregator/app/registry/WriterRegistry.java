package ru.wedwin.aggregator.app.registry;

import ru.wedwin.aggregator.domain.model.output.WriterId;
import ru.wedwin.aggregator.port.in.WriterCatalog;
import ru.wedwin.aggregator.port.out.Writer;
import ru.wedwin.aggregator.port.out.WriterProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// todo: think about registry and enum
public class WriterRegistry implements WriterCatalog, WriterProvider {
    private final Map<WriterId, Writer> byId;

    public WriterRegistry(List<Writer> writers) {
        byId = writers.stream().collect(Collectors.toMap(Writer::id, c -> c));
    }

    public List<WriterId> list() {
        return new ArrayList<>(byId.keySet());
    }

    public Writer getWriter(WriterId id) {
        Writer writer = byId.get(id);
        if (writer == null) {
            throw new IllegalArgumentException("unknown writer: " + id);
        }
        return writer;
    }

}
