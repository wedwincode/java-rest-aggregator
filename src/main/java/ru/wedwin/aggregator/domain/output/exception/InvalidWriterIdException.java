package ru.wedwin.aggregator.domain.output.exception;

public class InvalidWriterIdException extends IllegalArgumentException {
    public InvalidWriterIdException(String message) {
        super(message);
    }
}
