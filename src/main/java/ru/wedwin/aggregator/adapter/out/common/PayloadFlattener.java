package ru.wedwin.aggregator.adapter.out.common;

import ru.wedwin.aggregator.domain.result.Payload;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PayloadFlattener {

    private static final ObjectMapper om = new ObjectMapper();

    private PayloadFlattener() {
    }

    public static Map<String, String> flatten(Payload payload) {
        Map<String, String> out = new LinkedHashMap<>();
        flattenIntoMap(out, "payload", payload);

        return out;
    }

    private static void flattenIntoMap(Map<String, String> out, String path, Payload p) {
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

                o.fields().forEach((key, value) -> {
                    String childPath = path + "." + key;
                    flattenIntoMap(out, childPath, value);
                });

                return;
            }
            case Payload.PArray a -> {
                if (a.items() == null || a.items().isEmpty()) {
                    out.put(path, "");
                    return;
                }

                out.put(path, om.writeValueAsString(PayloadJsonConverter.toJson(a)));

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
        for (var e: flat.entrySet()) {
            String k = e.getKey();
            if (k == null) {
                continue;
            }
            if (k.equals("payload") || k.startsWith("payload.")) {
                payloadEntries.put(k, e.getValue());
            }
        }

        if (payloadEntries.isEmpty()) {
            return new Payload.PNull();
        }
        if (payloadEntries.size() == 1 && payloadEntries.containsKey("payload")) {
            return parseLeaf(payloadEntries.get("payload"));
        }

        Map<String, Payload> root = new LinkedHashMap<>();

        for (Map.Entry<String, String> e: payloadEntries.entrySet()) {
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
                    current.put(part, parseLeaf(e.getValue()));
                } else {
                    Payload existing = current.get(part);
                    if (existing instanceof Payload.PObject o) {
                        current = o.fields();
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

    private static Payload parseLeaf(String raw) {
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
                return PayloadJsonConverter.fromJson(node);
            }
        } catch (Exception _) {
        }

        if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
            return new Payload.PBool(Boolean.parseBoolean(s));
        }

        try {
            if (!s.contains(".")) {
                return new Payload.PInt(Integer.parseInt(s));
            }
        } catch (NumberFormatException _) {
        }

        try {
            return new Payload.PDouble(Double.parseDouble(s));
        } catch (NumberFormatException _) {
        }

        return new Payload.PString(s);
    }

}

