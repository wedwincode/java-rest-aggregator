package ru.wedwin.aggregator.domain.model.in;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.out.OutputSpec;

import java.util.Map;

public record RunConfig(
        Map<ApiId, ApiParams> apisWithParams,
        OutputSpec outputSpec
) {}
