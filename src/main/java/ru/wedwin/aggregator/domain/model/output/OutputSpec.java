package ru.wedwin.aggregator.domain.model.output;

import ru.wedwin.aggregator.domain.model.format.FormatterId;
import ru.wedwin.aggregator.domain.model.output.exception.InvalidOutputSpecException;

import java.nio.file.Path;

public record OutputSpec(Path path, FormatterId formatterId, WriteMode mode) {
    public OutputSpec {
        if (path == null) {
            throw new InvalidOutputSpecException("path is null");
        }
        if (formatterId == null) {
            throw new InvalidOutputSpecException("formatterId is null");
        }
        if (mode == null) {
            throw new InvalidOutputSpecException("mode is null");
        }
    }
}
