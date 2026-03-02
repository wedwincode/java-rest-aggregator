package ru.wedwin.aggregator.domain.model.api;

import ru.wedwin.aggregator.domain.model.api.exception.InvalidApiDefinitionException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public record ApiDefinition(
        ApiId id,
        URL url,
        String displayName,
        List<ParamMeta> supportedParams
) {
    public ApiDefinition {
        if (url == null) {
            throw new InvalidApiDefinitionException("url is null");
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

        displayName = displayName.trim();
    }

    public ApiDefinition(String id, String url, String displayName, ParamMeta... supportedParams) {
        this(new ApiId(id), toUrl(url), displayName, List.of(supportedParams));
    }

    private static URL toUrl(String url) {
        try {
            return new URI(url).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url: " + url, e);
        }
    }
}
