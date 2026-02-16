package ru.wedwin.aggregator.domain.model;

import java.nio.file.Path;

public record OutputSpec(Path path, OutputFormat format, WriteMode mode) {
}
