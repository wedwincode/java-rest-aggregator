package ru.wedwin.aggregator.domain.model.out;

import java.nio.file.Path;

public record OutputSpec(Path path, OutputFormat format, WriteMode mode) {
}
