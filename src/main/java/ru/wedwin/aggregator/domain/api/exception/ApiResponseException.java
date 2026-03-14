package ru.wedwin.aggregator.domain.api.exception;

public class ApiResponseException extends RuntimeException {
    public ApiResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
