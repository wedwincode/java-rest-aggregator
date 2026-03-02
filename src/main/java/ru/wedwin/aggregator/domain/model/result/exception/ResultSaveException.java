package ru.wedwin.aggregator.domain.model.result.exception;

public class ResultSaveException extends Exception {
    public ResultSaveException(String message) {
        super(message);
    }
    public ResultSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
