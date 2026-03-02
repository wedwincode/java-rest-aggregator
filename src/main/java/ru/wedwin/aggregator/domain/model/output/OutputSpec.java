package ru.wedwin.aggregator.domain.model.output;

import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.domain.model.output.exception.InvalidOutputSpecException;

import java.nio.file.Path;

public record OutputSpec(Path path, CodecId codecId, WriteMode mode) {
    public OutputSpec {
        if (path == null) {
            throw new InvalidOutputSpecException("path is null");
        }
        if (codecId == null) {
            throw new InvalidOutputSpecException("codecId is null");
        }
        if (mode == null) {
            throw new InvalidOutputSpecException("mode is null");
        }
    }
}
