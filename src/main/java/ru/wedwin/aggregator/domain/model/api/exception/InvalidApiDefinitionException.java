package ru.wedwin.aggregator.domain.model.api.exception;

public class InvalidApiDefinitionException extends IllegalArgumentException {
    public InvalidApiDefinitionException(String message) {
        super(message);
    }
}
