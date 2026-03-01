package ru.wedwin.aggregator.domain.model.output.exception;

public class InvalidOutputSpecException extends IllegalArgumentException {
    public InvalidOutputSpecException(String message) {
        super(message);
    }
}
