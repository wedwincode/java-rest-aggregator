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
public final class PayloadMapper { // todo rename to smth more common (e.g. PayloadTools)
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

    public static Map<String, String> flatten(Payload payload) {
        Map<String, String> out = new LinkedHashMap<>();
        flattenInto(out, "payload", payload);
        return out;
    }

    private static void flattenInto(Map<String, String> out, String path, Payload p) {
        switch (p) {
            case null -> {
                out.put(path, "");
                return;
            }
            case Payload.PNull _ -> {
                out.put(path, "");
                return;
            }
            case Payload.PBool b -> {
                out.put(path, String.valueOf(b.value()));
                return;
            }
            case Payload.PNumber n -> {
                out.put(path, String.valueOf(n.value()));
                return;
            }
            case Payload.PString s -> {
                out.put(path, s.value() == null ? "" : s.value());
                return;
            }
            case Payload.PObject o -> {
                if (o.fields() == null || o.fields().isEmpty()) {
                    out.put(path, "");
                    return;
                }
                for (var e : o.fields().entrySet()) {
                    String childPath = path + "." + e.getKey();
                    flattenInto(out, childPath, e.getValue());
                }
                return;
            }
            case Payload.PArray a -> {
                if (a.items() == null || a.items().isEmpty()) {
                    out.put(path, "");
                    return;
                }

                for (int i = 0; i < a.items().size(); i++) {
                    String childPath = path + "[" + i + "]";
                    flattenInto(out, childPath, a.items().get(i));
                }

                // вариант b: можно вместо индексации писать json строкой в одну ячейку
                // out.put(path, payloadToJsonString(p));
                return;
            }
            default -> {}
        }

        out.put(path, String.valueOf(p));
    }

}

