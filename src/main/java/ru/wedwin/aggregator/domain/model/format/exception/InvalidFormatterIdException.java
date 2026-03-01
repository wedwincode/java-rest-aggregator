package ru.wedwin.aggregator.domain.model.format.exception;

public class InvalidFormatterIdException extends IllegalArgumentException {
    public InvalidFormatterIdException(String message) {
        super(message);
    }
}
