package ru.wedwin.aggregator.domain.config.exception;

public class InvalidRunConfigException extends IllegalArgumentException {
    public InvalidRunConfigException(String message) {
        super(message);
    }
}
