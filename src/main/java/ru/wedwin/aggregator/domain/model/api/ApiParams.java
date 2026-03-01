package ru.wedwin.aggregator.domain.model.api;

import ru.wedwin.aggregator.domain.model.api.exception.InvalidApiParamsException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ApiParams {
    // TODO: change string to smth more special (param, value)
    private final Map<String, String> params;

    private ApiParams(Map<String, String> params) {
        this.params = new LinkedHashMap<>(params);
    }

    public static ApiParams of() {
        return new ApiParams(Map.of());
    }

    public static ApiParams of(Map<String, String> params) {
        return new ApiParams(params == null ? Map.of() : params);
    }

    public String put(String key, String value) {
        if (key == null || key.isBlank()) {
            throw new InvalidApiParamsException("key is empty");
        }
        if (value == null || value.isBlank()) {
            throw new InvalidApiParamsException("value is empty");
        }
        return params.put(key, value);
    }

    public void addDefaultParams(List<ParamMeta> paramMetas) {
        if (paramMetas == null) {
            return;
        }
        for (ParamMeta param: paramMetas) {
            if (!params.containsKey(param.key()) && param.required()) {
                if (param.defaultValue() == null || param.defaultValue().isBlank()) {
                    throw new InvalidApiParamsException("param " + param.key() + " is null");
                }
                params.put(param.key(), param.defaultValue());
            }
        }
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public String toString() {
        return "ApiParams{" +
                "params=" + params +
                '}';
    }
}