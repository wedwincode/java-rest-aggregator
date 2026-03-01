package ru.wedwin.aggregator.domain.model.output;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.output.exception.InvalidDisplaySpecException;

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
