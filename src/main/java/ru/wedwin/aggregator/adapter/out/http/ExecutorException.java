package ru.wedwin.aggregator.adapter.out.http;

public class ExecutorException extends RuntimeException {
    public ExecutorException(String message) {
        super(message);
    }
    public ExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
