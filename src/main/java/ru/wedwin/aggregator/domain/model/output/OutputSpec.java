package ru.wedwin.aggregator.domain.model.output;

import ru.wedwin.aggregator.domain.model.output.exception.InvalidOutputSpecException;

import java.nio.file.Path;

public record OutputSpec(Path path, WriterId writerId, WriteMode mode) {
    public OutputSpec {
        if (path == null) {
            throw new InvalidOutputSpecException("path is null");
        }
        if (writerId == null) {
            throw new InvalidOutputSpecException("writerId is null");
        }
        if (mode == null) {
            throw new InvalidOutputSpecException("mode is null");
        }
    }
}
