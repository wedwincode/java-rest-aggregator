package ru.wedwin.aggregator.domain.api;

import ru.wedwin.aggregator.domain.api.exception.InvalidParamMetaException;

public record ParamMeta(String key, boolean required, String defaultValue, String description) {
    public ParamMeta {
        if (key == null) {
            throw new InvalidParamMetaException("param key is null");
        }
        if (key.isBlank()) {
            throw new InvalidParamMetaException("param key is empty");
        }

        key = key.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return "key=" + key +
                ", required=" + required +
                ", defaultValue=" + defaultValue +
                ", description=" + description;
    }
}
