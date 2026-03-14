package ru.wedwin.aggregator.domain.api.exception;

public class InvalidApiParamsException extends IllegalArgumentException {
    public InvalidApiParamsException(String message) {
        super(message);
    }
}
