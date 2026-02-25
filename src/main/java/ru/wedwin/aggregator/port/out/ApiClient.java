package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.ApiId;
import ru.wedwin.aggregator.domain.model.ApiParams;
import ru.wedwin.aggregator.domain.model.ParamSpec;

import java.util.List;

public interface ApiClient {
    ApiId id();
    String url();
    String displayName();
    List<ParamSpec> supportedParams();

    List<AggregatedRecord> getApiResponse(ApiParams params, HttpExecutor executor);
}
