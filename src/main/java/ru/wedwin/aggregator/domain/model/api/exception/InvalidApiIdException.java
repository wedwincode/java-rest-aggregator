package ru.wedwin.aggregator.domain.model.api.exception;

public class InvalidApiIdException extends IllegalArgumentException {
    public InvalidApiIdException(String message) {
        super(message);
    }
}
