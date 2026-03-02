package ru.wedwin.aggregator.domain.model.codec.exception;

import java.io.IOException;

public class CodecException extends IOException {
    public CodecException(String message) {
        super(message);
    }
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
