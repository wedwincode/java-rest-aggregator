package ru.wedwin.aggregator.domain.model.api;

import ru.wedwin.aggregator.domain.model.api.exception.InvalidParamMetaException;

public record ParamMeta(String key, boolean required, String defaultValue, String description) {
    public ParamMeta {
        if (key == null || key.isBlank()) {
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
