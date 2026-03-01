package ru.wedwin.aggregator.adapter.out.formatters;

import ru.wedwin.aggregator.adapter.out.common.PayloadMapper;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;
import ru.wedwin.aggregator.domain.model.format.FormatterId;
import ru.wedwin.aggregator.port.out.Formatter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonFormatter implements Formatter {
    private final ObjectMapper om;

    public JsonFormatter() {
        om = new ObjectMapper();
    }

    @Override
    public FormatterId id() {
        return new FormatterId("json"); // todo change "new" to .of()
    }

    // todo null checks everywhere
    @Override
    public void format(List<AggregatedItem> items, OutputSpec spec) {
        if (items == null || spec == null) {
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
            items.stream().map(this::itemToObjectNode).forEach(array::add);
            om.writerWithDefaultPrettyPrinter().writeValue(spec.path().toFile(), array);
        } catch (IOException e) {
            throw new RuntimeException("failed to write output to " + spec.path(), e);
        }
    }

    private ObjectNode itemToObjectNode(AggregatedItem item) {
        ObjectNode obj = om.createObjectNode();
        obj.put("itemId", item.itemId().toString());
        obj.put("apiId", item.apiId().toString());
        obj.put("fetchedAt", item.fetchedAt().toString());
        obj.set("payload", PayloadMapper.toJsonNode(item.payload(), om));
        return obj;
    }

    private ArrayNode readArrayOrCreateEmpty(Path file) throws IOException {
        if (!Files.exists(file) || Files.size(file) == 0) {
            return om.createArrayNode();
        }
        String content = Files.readString(file, StandardCharsets.UTF_8).trim();
        if (content.isBlank()) {
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
