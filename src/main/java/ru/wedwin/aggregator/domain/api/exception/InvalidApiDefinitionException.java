package ru.wedwin.aggregator.domain.api.exception;

public class InvalidApiDefinitionException extends IllegalArgumentException {
    public InvalidApiDefinitionException(String message) {
        super(message);
    }
}
