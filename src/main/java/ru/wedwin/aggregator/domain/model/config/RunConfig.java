package ru.wedwin.aggregator.domain.model.config;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.config.exception.InvalidRunConfigException;
import ru.wedwin.aggregator.domain.model.format.FormatterId;
import ru.wedwin.aggregator.domain.model.output.DisplaySpec;
import ru.wedwin.aggregator.domain.model.output.OutputSpec;

import java.util.Map;

public record RunConfig(
        Map<ApiId, ApiParams> apisWithParams, // todo change name
        OutputSpec outputSpec,
        DisplaySpec displaySpec
) {
    public RunConfig {
        if (apisWithParams == null) {
            throw new InvalidRunConfigException("apisWithParams is null");
        }
        if (apisWithParams.isEmpty()) {
            throw new InvalidRunConfigException("no APIs selected");
        }
        if (apisWithParams.containsKey(null)) {
            throw new InvalidRunConfigException("api id is null");
        }
        if (apisWithParams.containsValue(null)) {
            throw new InvalidRunConfigException("api params is null");
        }
        if (outputSpec == null) {
            throw new InvalidRunConfigException("outputSpec is null");
        }
        if (displaySpec == null) {
            throw new InvalidRunConfigException("displaySpec is null");
        }

        apisWithParams = Map.copyOf(apisWithParams);
    }
}
