package ru.wedwin.aggregator.domain.result.exception;

public class ResultSaveException extends RuntimeException {
    public ResultSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
