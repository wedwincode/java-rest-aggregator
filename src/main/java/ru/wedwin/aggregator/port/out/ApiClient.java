package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.api.ParamSpec;

import java.util.List;

public interface ApiClient {
    ApiId id();
    String url();
    String displayName();
    List<ParamSpec> supportedParams();
    AggregatedRecord getApiResponse(ApiParams params, HttpExecutor executor);
}
