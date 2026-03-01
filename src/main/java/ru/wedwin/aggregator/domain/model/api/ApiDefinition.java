package ru.wedwin.aggregator.domain.model.api;

import java.net.URL;
import java.util.List;

public record ApiDefinition(
        ApiId id,
        String url, // todo URL url,
        String displayName,
        List<ParamSpec> supportedParams
) {
    public ApiDefinition {
    }

    public ApiDefinition(String id, String url, String displayName, ParamSpec... supportedParams) {
        this(new ApiId(id), url, displayName, List.of(supportedParams));
    }
}
