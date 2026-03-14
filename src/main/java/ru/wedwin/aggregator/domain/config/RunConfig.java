package ru.wedwin.aggregator.domain.config;

import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.config.exception.InvalidRunConfigException;
import ru.wedwin.aggregator.domain.output.DisplaySpec;
import ru.wedwin.aggregator.domain.output.ExecutionSpec;
import ru.wedwin.aggregator.domain.output.OutputSpec;

import java.util.Map;

public record RunConfig(
        Map<ApiId, ApiParams> queryParamsByApi,
        OutputSpec outputSpec,
        ExecutionSpec executionSpec,
        DisplaySpec displaySpec
) {
    public RunConfig {
        if (queryParamsByApi == null) {
            throw new InvalidRunConfigException("queryParamsByApi is null");
        }
        if (queryParamsByApi.isEmpty()) {
            throw new InvalidRunConfigException("no APIs selected");
        }
        if (outputSpec == null) {
            throw new InvalidRunConfigException("outputSpec is null");
        }
        if (executionSpec == null) {
            throw new InvalidRunConfigException("executionSpec is null");
        }
        if (displaySpec == null) {
            throw new InvalidRunConfigException("displaySpec is null");
        }

        try {
            queryParamsByApi = Map.copyOf(queryParamsByApi);
        } catch (NullPointerException e) {
            throw new InvalidRunConfigException("api id or api params is null");
        }
    }
}
