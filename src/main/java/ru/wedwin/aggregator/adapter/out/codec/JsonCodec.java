package ru.wedwin.aggregator.adapter.out.codec;

import ru.wedwin.aggregator.adapter.out.common.PayloadMapper;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.domain.model.result.Payload;
import ru.wedwin.aggregator.port.out.Codec;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class JsonCodec implements Codec {
    private final ObjectMapper om;

    public JsonCodec() {
        om = new ObjectMapper();
    }

    @Override
    public CodecId id() {
        return new CodecId("json"); // todo change "new" to .of()
    }

    @Override
    public List<AggregatedItem> read(Reader r) {
        ArrayNode array = readArrayOrCreateEmpty(r);

        List<AggregatedItem> items = new java.util.ArrayList<>(array.size());
        for (JsonNode n: array) {
            if (n == null || n.isNull()) {
                continue;
            }
            if (!n.isObject()) {
                throw new IllegalStateException("json array element is not an object");
            }
            items.add(toItem((ObjectNode) n));
        }
        return items;
    }

    @Override
    public void write(List<AggregatedItem> items, Writer w) {
        ArrayNode array = om.createArrayNode();
        items.stream().map(this::toNode).forEach(array::add);
        om.writerWithDefaultPrettyPrinter().writeValue(w, array);
    }


    private ArrayNode readArrayOrCreateEmpty(Reader r) {
        JsonNode root = om.readTree(r);
        if (root == null || root.isNull()) {
            return om.createArrayNode();
        }
        if (!root.isArray()) {
            throw new IllegalStateException("no json array found");
        }
        return (ArrayNode) root;
    }

    private ObjectNode toNode(AggregatedItem item) {
        ObjectNode obj = om.createObjectNode();
        obj.put("itemId", item.itemId().toString());
        obj.put("apiId", item.apiId().toString());
        obj.put("fetchedAt", item.fetchedAt().toString());
        obj.set("payload", PayloadMapper.toJsonNode(item.payload()));
        return obj;
    }

    private AggregatedItem toItem(ObjectNode obj) {
        UUID itemId = UUID.fromString(required(obj, "itemId"));
        ApiId apiId = new ApiId(required(obj, "apiId"));
        Instant fetchedAt = Instant.parse(required(obj, "fetchedAt"));

        JsonNode payloadNode = obj.get("payload");
        Payload payload = PayloadMapper.fromJsonNode(payloadNode);

        return new AggregatedItem(itemId, apiId, fetchedAt, payload);
    }

    private static String required(ObjectNode obj, String field) {
        JsonNode n = obj.get(field);
        if (n == null || n.isNull() || !n.isString() || n.asString().isBlank()) {
            throw new IllegalStateException("missing or invalid field: " + field);
        }
        return n.asString();
    }
}
