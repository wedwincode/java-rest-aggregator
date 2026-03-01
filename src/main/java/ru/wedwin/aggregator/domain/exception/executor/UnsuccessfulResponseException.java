package ru.wedwin.aggregator.domain.exception.executor;

public class UnsuccessfulResponseException extends RuntimeException {
    public UnsuccessfulResponseException() {
        super("response was unsuccessful");
    }
}
