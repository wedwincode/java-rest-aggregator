package ru.wedwin.aggregator.domain.model.api;

public record ParamSpec(String key, boolean required, String defaultValue, String description) {
    @Override
    public String toString() {
        return "key=" + key +
                ", required=" + required +
                ", defaultValue=" + defaultValue +
                ", description=" + description;
    }
}
