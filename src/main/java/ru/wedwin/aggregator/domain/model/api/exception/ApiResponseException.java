package ru.wedwin.aggregator.domain.model.api.exception;

public class ApiResponseException extends Exception {
    public ApiResponseException(String message) {
        super(message);
    }
    public ApiResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
