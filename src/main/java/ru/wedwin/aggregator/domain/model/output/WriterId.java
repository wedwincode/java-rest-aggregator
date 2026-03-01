package ru.wedwin.aggregator.domain.model.output;

import ru.wedwin.aggregator.domain.model.output.exception.InvalidWriterIdException;

public record WriterId(
        String value
) {
    public WriterId {
        if (value == null) { // todo: null/empty check for all classes
            throw new InvalidWriterIdException("value is null");
        }
        if (value.isBlank()) {
            throw new InvalidWriterIdException("value is empty");
        }

        value = value.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }
}
