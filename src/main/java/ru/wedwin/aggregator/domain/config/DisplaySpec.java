package ru.wedwin.aggregator.domain.config;

import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.config.exception.InvalidDisplaySpecException;

public record DisplaySpec(
        ApiId apiId,
        DisplayMode mode
) {

    public DisplaySpec {
        if (mode == null) {
            throw new InvalidDisplaySpecException("mode is null");
        }
    }

    public DisplaySpec(DisplayMode mode) {
        this(null, mode);
    }
}
