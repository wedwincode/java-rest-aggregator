package ru.wedwin.aggregator.domain.model.codec.exception;

public class InvalidCodecIdException extends IllegalArgumentException {
    public InvalidCodecIdException(String message) {
        super(message);
    }
}
