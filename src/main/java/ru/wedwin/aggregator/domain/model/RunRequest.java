package ru.wedwin.aggregator.domain.model;

import java.util.Map;

public record RunRequest(
        Map<ApiId, ApiParams> apisWithParams,
        OutputSpec outputSpec
) {}
