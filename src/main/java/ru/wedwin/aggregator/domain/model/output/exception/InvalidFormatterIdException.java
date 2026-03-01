package ru.wedwin.aggregator.domain.model.output.exception;

public class InvalidFormatterIdException extends IllegalArgumentException {
    public InvalidFormatterIdException(String message) {
        super(message);
    }
}
