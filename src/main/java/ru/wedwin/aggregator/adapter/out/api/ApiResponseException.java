package ru.wedwin.aggregator.adapter.out.api;

public class ApiResponseException extends RuntimeException {
    public ApiResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
