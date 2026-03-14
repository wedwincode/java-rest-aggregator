package ru.wedwin.aggregator.domain.api.exception;

public class InvalidParamMetaException extends IllegalArgumentException {
    public InvalidParamMetaException(String message) {
        super(message);
    }
}
