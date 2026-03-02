package ru.wedwin.aggregator.domain.model.codec;

import ru.wedwin.aggregator.domain.model.codec.exception.InvalidCodecIdException;

public record CodecId(
        String value
) {
    public CodecId {
        if (value == null) {
            throw new InvalidCodecIdException("value is null");
        }
        if (value.isBlank()) {
            throw new InvalidCodecIdException("value is empty");
        }

        value = value.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }
}
