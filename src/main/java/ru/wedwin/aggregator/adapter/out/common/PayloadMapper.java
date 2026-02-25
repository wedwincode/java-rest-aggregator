package ru.wedwin.aggregator.adapter.out.common;

import ru.wedwin.aggregator.domain.model.Payload;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
}

