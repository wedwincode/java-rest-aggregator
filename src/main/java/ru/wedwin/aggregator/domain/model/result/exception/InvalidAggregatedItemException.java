package ru.wedwin.aggregator.domain.model.result.exception;

public class InvalidAggregatedItemException extends IllegalArgumentException {
    public InvalidAggregatedItemException(String message) {
        super(message);
    }
}
