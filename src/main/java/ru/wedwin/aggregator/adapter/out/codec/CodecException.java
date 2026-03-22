package ru.wedwin.aggregator.adapter.out.codec;

import java.io.IOException;

public class CodecException extends IOException {
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
