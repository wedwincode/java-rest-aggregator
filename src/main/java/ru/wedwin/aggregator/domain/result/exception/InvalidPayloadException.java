package ru.wedwin.aggregator.domain.result.exception;

public class InvalidPayloadException extends IllegalArgumentException {
    public InvalidPayloadException(String message) {
        super(message);
    }
}
