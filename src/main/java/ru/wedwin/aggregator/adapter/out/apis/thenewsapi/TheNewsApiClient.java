package ru.wedwin.aggregator.adapter.out.apis.thenewsapi;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.ApiId;
import ru.wedwin.aggregator.domain.model.ApiParams;
import ru.wedwin.aggregator.domain.model.ParamSpec;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.HttpExecutor;

import java.util.List;

public class TheNewsApiClient implements ApiClient {
    @Override
    public ApiId id() {
        return null;
    }

    @Override
    public String url() {
        return "";
    }

    @Override
    public String displayName() {
        return "";
    }

    @Override
    public List<ParamSpec> supportedParams() {
        return List.of();
    }

    @Override
    public AggregatedRecord getApiResponse(ApiParams params, HttpExecutor executor) {
        return null;
    }
}
