package ru.wedwin.aggregator.domain.model.api;

import ru.wedwin.aggregator.domain.model.api.exception.InvalidApiIdException;

import java.util.Objects;

public record ApiId(String value) {
    public ApiId(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidApiIdException();
        }
        this.value = value.trim().toLowerCase();
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
