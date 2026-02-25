package ru.wedwin.aggregator.domain.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ApiParams {
    // TODO: change string to smth more special (param, value)
    private final Map<String, String> values;

    private ApiParams(Map<String, String> values) {
        this.values = new LinkedHashMap<>(values);
    }

    public static ApiParams of(Map<String, String> values) {
        return new ApiParams(values == null ? Map.of() : values);
    }

    public String get(String key) {
        return values.get(key);
    }

    public String getOrDefault(String key, String def) {
        String v = values.get(key);
        return v == null || v.isBlank() ? def : v;
    }

    public void addDefaultParams(List<ParamSpec> spec) {
        for (ParamSpec param: spec) {
            if (!values.containsKey(param.key()) && param.required()) {
                values.put(param.key(), param.defaultValue());
            }
        }
    }

    public String put(String key, String value) {
        return values.put(key, value);
    }

    public Map<String, String> asMap() {
        return values;
    }

    @Override
    public String toString() {
        return "ApiParams{" +
                "values=" + values +
                '}';
    }
}