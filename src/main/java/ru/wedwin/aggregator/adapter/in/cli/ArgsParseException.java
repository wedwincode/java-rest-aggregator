package ru.wedwin.aggregator.adapter.in.cli;

public class ArgsParseException extends RuntimeException {
    public ArgsParseException(String message) {
        super(message);
    }
    public ArgsParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
