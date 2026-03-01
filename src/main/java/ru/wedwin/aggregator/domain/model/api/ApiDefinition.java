package ru.wedwin.aggregator.domain.model.api;

import ru.wedwin.aggregator.domain.model.api.exception.InvalidApiDefinitionException;

import java.util.List;
import java.util.Objects;

public record ApiDefinition(
        ApiId id,
        String url, // todo URL url,
        String displayName,
        List<ParamMeta> supportedParams
) {
    public ApiDefinition {
        if (url == null) {
            throw new InvalidApiDefinitionException("url is null");
        }
        if (url.isBlank()) {
            throw new InvalidApiDefinitionException("url is empty");
        }
        if (displayName == null) {
            throw new InvalidApiDefinitionException("displayName is null");
        }
        if (displayName.isBlank()) {
            throw new InvalidApiDefinitionException("displayName is empty");
        }
        if (supportedParams == null) {
            throw new InvalidApiDefinitionException("supportedParams is null");
        }
        if (supportedParams.stream().anyMatch(Objects::isNull)) {
            throw new InvalidApiDefinitionException("supportedParams contains null");
        }

        url = url.trim();
        displayName = displayName.trim();
    }

    public ApiDefinition(String id, String url, String displayName, ParamMeta... supportedParams) {
        this(new ApiId(id), url, displayName, List.of(supportedParams));
    }
}
