package ru.wedwin.aggregator.domain.config.exception;

public class InvalidWriterIdException extends IllegalArgumentException {
    public InvalidWriterIdException(String message) {
        super(message);
    }
}
