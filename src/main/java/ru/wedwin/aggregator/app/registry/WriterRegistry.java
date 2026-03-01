package ru.wedwin.aggregator.app.registry;

import ru.wedwin.aggregator.port.out.OutputWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// todo: think about registry and enum
public class WriterRegistry {
    private final Map<String, OutputWriter> byId;

    public WriterRegistry(List<OutputWriter> writers) {
        byId = writers.stream().collect(Collectors.toMap(c -> c.id().toLowerCase(), c -> c));
    }

    public OutputWriter require(String id) {
        OutputWriter writer = byId.get(id.toLowerCase());
        if (writer == null) {
            throw new IllegalArgumentException("unknown writer: " + id);
        }
        return writer;
    }

    public List<OutputWriter> all() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public String toString() {
        return String.join(" ", byId.keySet());
    }
}
