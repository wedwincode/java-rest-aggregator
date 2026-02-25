package ru.wedwin.aggregator.domain.exceptions.executor;

public class ExecutorException extends RuntimeException { // todo is it ok to put exceptions in domain
    public ExecutorException(String message) {
        super(message);
    }
}
