package ru.wedwin.aggregator.domain.model.api;

import java.util.Objects;

public record ApiId(String value) {
    public ApiId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("api id is empty");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ApiId apiId = (ApiId) o;
        return Objects.equals(value, apiId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
