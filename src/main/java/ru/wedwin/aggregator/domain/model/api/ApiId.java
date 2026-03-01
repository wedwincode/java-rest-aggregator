package ru.wedwin.aggregator.domain.model.api;

import ru.wedwin.aggregator.domain.model.api.exception.InvalidApiIdException;

public record ApiId(String value) {
    public ApiId {
        if (value == null) {
            throw new InvalidApiIdException("value is null");
        }
        if (value.isBlank()) {
            throw new InvalidApiIdException("value is empty");
        }

        value = value.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }
}
