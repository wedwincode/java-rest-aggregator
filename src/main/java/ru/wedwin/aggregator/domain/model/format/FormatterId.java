package ru.wedwin.aggregator.domain.model.format;

import ru.wedwin.aggregator.domain.model.format.exception.InvalidFormatterIdException;

public record FormatterId(
        String value
) {
    public FormatterId {
        if (value == null) { // todo: null/empty check for all classes
            throw new InvalidFormatterIdException("value is null");
        }
        if (value.isBlank()) {
            throw new InvalidFormatterIdException("value is empty");
        }

        value = value.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }
}
