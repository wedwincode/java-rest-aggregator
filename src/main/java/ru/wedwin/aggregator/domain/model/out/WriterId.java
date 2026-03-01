package ru.wedwin.aggregator.domain.model.out;

import java.util.Objects;

public record WriterId(
        String value
) {
    public WriterId(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("writer id is empty");
        this.value = value.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WriterId apiId = (WriterId) o;
        return Objects.equals(value, apiId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
