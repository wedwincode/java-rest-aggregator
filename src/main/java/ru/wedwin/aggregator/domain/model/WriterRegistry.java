package ru.wedwin.aggregator.domain.model;

import ru.wedwin.aggregator.port.out.OutputWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WriterRegistry {
    private final Map<String, OutputWriter> byId;

    public WriterRegistry(List<OutputWriter> writers) {
        byId = writers.stream().collect(Collectors.toMap(OutputWriter::id, c -> c));
    }

    public OutputWriter require(String id) {
        OutputWriter writer = byId.get(id);
        if (writer == null) {
            throw new IllegalArgumentException("unknown writer: " + id);
        }
        return writer;
    }

    public List<OutputWriter> all() {
        return new ArrayList<>(byId.values());
    }
}
