package ru.wedwin.aggregator.domain.result.exception;

public class InvalidAggregatedItemException extends IllegalArgumentException {
    public InvalidAggregatedItemException(String message) {
        super(message);
    }
}
