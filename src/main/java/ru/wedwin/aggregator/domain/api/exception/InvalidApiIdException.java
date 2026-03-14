package ru.wedwin.aggregator.domain.api.exception;

public class InvalidApiIdException extends IllegalArgumentException {
    public InvalidApiIdException(String message) {
        super(message);
    }
}
