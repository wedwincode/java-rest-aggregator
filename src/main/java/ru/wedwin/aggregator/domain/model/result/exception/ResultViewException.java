package ru.wedwin.aggregator.domain.model.result.exception;

public class ResultViewException extends Exception {
    public ResultViewException(String message) {
        super(message);
    }
    public ResultViewException(String message, Throwable cause) {
        super(message, cause);
    }
}
