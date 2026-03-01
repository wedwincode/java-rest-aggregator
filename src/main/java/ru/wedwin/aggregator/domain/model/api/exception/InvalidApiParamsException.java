package ru.wedwin.aggregator.domain.model.api.exception;

public class InvalidApiParamsException extends IllegalArgumentException {
    public InvalidApiParamsException(String message) {
        super(message);
    }
}
