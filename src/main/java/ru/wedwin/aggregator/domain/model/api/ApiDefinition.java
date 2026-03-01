package ru.wedwin.aggregator.domain.model.api;

import java.util.List;

public record ApiDefinition(
        ApiId id,
        String url, // todo URL url,
        String displayName,
        List<ParamMeta> supportedParams
) {
    public ApiDefinition {
    }

    public ApiDefinition(String id, String url, String displayName, ParamMeta... supportedParams) {
        this(new ApiId(id), url, displayName, List.of(supportedParams));
    }
}
