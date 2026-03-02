package ru.wedwin.aggregator.adapter.out.common;

import ru.wedwin.aggregator.domain.model.result.Payload;
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
    private static final ObjectMapper om = new ObjectMapper();

    private PayloadMapper() {
    }

    public static Payload fromJsonNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return new Payload.PNull();
        }
        if (node.isBoolean()) {
            return new Payload.PBool(node.booleanValue());
        }
        if (node.isInt()) {
            return new Payload.PInt(node.intValue());
        }
        if (node.isNumber()) {
            return new Payload.PDouble(node.doubleValue());
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

    public static JsonNode toJsonNode(Payload payload) {
        JsonNodeFactory f = om.getNodeFactory();

        if (payload == null) return f.nullNode();

        return switch (payload) {
            case Payload.PNull _ -> f.nullNode();
            case Payload.PBool b -> f.booleanNode(b.value());
            case Payload.PInt num -> f.numberNode(num.value());
            case Payload.PDouble num -> f.numberNode(num.value());
            case Payload.PString s -> f.stringNode(s.value());

            case Payload.PArray a -> {
                ArrayNode arr = om.createArrayNode();
                for (Payload item : a.items()) {
                    arr.add(toJsonNode(item));
                }
                yield arr;
            }

            case Payload.PObject o -> {
                ObjectNode obj = om.createObjectNode();
                o.fields().forEach((k, v) -> obj.set(k, toJsonNode(v)));
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
            case Payload.PInt n -> {
                out.put(path, String.valueOf(n.value()));
                return;
            }
            case Payload.PDouble n -> {
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

//                for (int i = 0; i < a.items().size(); i++) {
//                    String childPath = path + "[" + i + "]";
//                    flattenInto(out, childPath, a.items().get(i));
//                }

                out.put(path, om.writeValueAsString(toJsonNode(a)));
                return;
            }
            default -> {}
        }

        out.put(path, String.valueOf(p));
    }

    public static Payload unflatten(Map<String, String> flat) {
        if (flat == null || flat.isEmpty()) {
            return new Payload.PNull();
        }

        Map<String, String> payloadEntries = new LinkedHashMap<>();
        for (var e : flat.entrySet()) {
            String k = e.getKey();
            if (k == null) continue;
            if (k.equals("payload") || k.startsWith("payload.")) {
                payloadEntries.put(k, e.getValue());
            }
        }
        if (payloadEntries.isEmpty()) {
            return new Payload.PNull();
        }

        if (payloadEntries.size() == 1 && payloadEntries.containsKey("payload")) {
            return parsePayloadLeaf(payloadEntries.get("payload"));
        }

        Map<String, Payload> root = new LinkedHashMap<>();

        for (var e : payloadEntries.entrySet()) {
            String key = e.getKey();
            if (key.equals("payload")) {
                continue;
            }

            String path = key.substring("payload.".length());
            String[] parts = path.split("\\.");

            Map<String, Payload> current = root;
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                boolean last = (i == parts.length - 1);

                if (last) {
                    current.put(part, parsePayloadLeaf(e.getValue()));
                } else {
                    Payload existing = current.get(part);
                    if (existing instanceof Payload.PObject(Map<String, Payload> fields)) {
                        current = fields;
                    } else {
                        Map<String, Payload> next = new LinkedHashMap<>();
                        current.put(part, new Payload.PObject(next));
                        current = next;
                    }
                }
            }
        }

        return new Payload.PObject(root);
    }

    private static Payload parsePayloadLeaf(String raw) {
        if (raw == null) {
            return new Payload.PNull();
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return new Payload.PNull();
        }

        try {
            JsonNode node = om.readTree(s);

            if (node != null) {
                return fromJsonNode(node);
            }
        } catch (Exception _) {}

        if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
            return new Payload.PBool(Boolean.parseBoolean(s));
        }
        try {
            if (!s.contains(".")) {
                return new Payload.PInt(Integer.parseInt(s));
            }
        } catch (NumberFormatException _) {}

        try {
            return new Payload.PDouble(Double.parseDouble(s));
        } catch (NumberFormatException _) {}

        return new Payload.PString(s);
    }

}

