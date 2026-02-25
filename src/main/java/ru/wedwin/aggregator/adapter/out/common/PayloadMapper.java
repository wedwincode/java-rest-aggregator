package ru.wedwin.aggregator.adapter.out.common;

import ru.wedwin.aggregator.domain.model.Payload;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// todo разобраться что здесь происходит
public final class PayloadMapper {
    private PayloadMapper() {
    }
    public static Payload fromJsonNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return new Payload.PNull();
        }
        if (node.isBoolean()) {
            return new Payload.PBool(node.booleanValue());
        }
        if (node.isNumber()) {
            return new Payload.PNumber(node.doubleValue());
        }
        if (node.isString()) {
            return new Payload.PString(node.asString());
        }

        if (node.isArray()) {
            List<Payload> items = new ArrayList<>();
            for (JsonNode item : node) {
                items.add(fromJsonNode(item));
            }
            return new Payload.PArray(items);
        }

        if (node.isObject()) {
            Map<String, Payload> fields = new LinkedHashMap<>();
            for (Map.Entry<String, JsonNode> e : node.properties()) {
                fields.put(e.getKey(), fromJsonNode(e.getValue()));
            }
            return new Payload.PObject(fields);
        }

        return new Payload.PString(node.asString());
    }

    public static JsonNode toJsonNode(Payload payload, ObjectMapper om) {
        JsonNodeFactory f = om.getNodeFactory();

        if (payload == null) return f.nullNode();

        return switch (payload) {
            case Payload.PNull _ -> f.nullNode();
            case Payload.PBool b -> f.booleanNode(b.value());
            case Payload.PNumber num -> f.numberNode(num.value());
            case Payload.PString s -> f.stringNode(s.value());

            case Payload.PArray a -> {
                ArrayNode arr = om.createArrayNode();
                for (Payload item : a.items()) {
                    arr.add(toJsonNode(item, om));
                }
                yield arr;
            }

            case Payload.PObject o -> {
                ObjectNode obj = om.createObjectNode();
                o.fields().forEach((k, v) -> obj.set(k, toJsonNode(v, om)));
                yield obj;
            }
        };
    }

}

