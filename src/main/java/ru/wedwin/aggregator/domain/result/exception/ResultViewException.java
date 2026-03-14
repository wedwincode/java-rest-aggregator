package ru.wedwin.aggregator.domain.result.exception;

public class ResultViewException extends Exception {
    public ResultViewException(String message) {
        super(message);
    }
    public ResultViewException(String message, Throwable cause) {
        super(message, cause);
    }
}
