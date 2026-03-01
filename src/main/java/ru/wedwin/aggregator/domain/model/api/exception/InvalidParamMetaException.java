package ru.wedwin.aggregator.domain.model.api.exception;

public class InvalidParamMetaException extends IllegalArgumentException {
    public InvalidParamMetaException(String message) {
        super(message);
    }
}
