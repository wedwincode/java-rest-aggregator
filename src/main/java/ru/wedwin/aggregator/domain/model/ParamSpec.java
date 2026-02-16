package ru.wedwin.aggregator.domain.model;

public record ParamSpec(String key, boolean required, String defaultValue, String description) {
}
