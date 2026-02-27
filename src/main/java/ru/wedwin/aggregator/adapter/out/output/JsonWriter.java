package ru.wedwin.aggregator.adapter.out.output;

import ru.wedwin.aggregator.adapter.out.common.PayloadMapper;
import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.out.OutputSpec;
import ru.wedwin.aggregator.port.out.OutputWriter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonWriter implements OutputWriter {
    private final ObjectMapper om;

    public JsonWriter() {
        om = new ObjectMapper();
    }

    @Override
    public String id() {
        return "json";
    }

    // todo null checks everywhere
    @Override
    public void write(List<AggregatedRecord> records, OutputSpec spec) {
        if (records == null || spec == null) {
            return;
        }
        try {
            if (spec.path().getParent() != null) {
                Files.createDirectories(spec.path().getParent());
            }
            ArrayNode array;
            switch (spec.mode()) {
                case NEW -> array = om.createArrayNode();
                case APPEND -> array = readArrayOrCreateEmpty(spec.path());
                case null, default -> throw new RuntimeException("null!!"); // todo everywhere
            }
            records.stream().map(this::recordToObjectNode).forEach(array::add);
            om.writerWithDefaultPrettyPrinter().writeValue(spec.path().toFile(), array);
        } catch (IOException e) {
            throw new RuntimeException("failed to write output to " + spec.path(), e);
        }
    }

    private ObjectNode recordToObjectNode(AggregatedRecord record) {
        ObjectNode obj = om.createObjectNode();
        obj.put("itemId", record.itemId().toString());
        obj.put("apiId", record.apiId().toString());
        obj.put("timestamp", record.timestamp().toString());
        obj.set("payload", PayloadMapper.toJsonNode(record.payload(), om));
        return obj;
    }

    private ArrayNode readArrayOrCreateEmpty(Path file) throws IOException {
        if (!Files.exists(file) || Files.size(file) == 0) {
            return om.createArrayNode();
        }
        String content = Files.readString(file, StandardCharsets.UTF_8).trim();
        if (content.isEmpty()) {
            return om.createArrayNode();
        }
        JsonNode root = om.readTree(content);
        if (root == null || root.isNull()) {
            return om.createArrayNode();
        }
        if (!root.isArray()) {
            throw new IllegalStateException("no json array found in file:" + file);
        }
        return (ArrayNode) root;
    }
}
