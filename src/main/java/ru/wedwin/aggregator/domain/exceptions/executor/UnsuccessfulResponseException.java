package ru.wedwin.aggregator.domain.exceptions.executor;

public class UnsuccessfulResponseException extends RuntimeException {
    public UnsuccessfulResponseException() {
        super("response was unsuccessful");
    }
}
