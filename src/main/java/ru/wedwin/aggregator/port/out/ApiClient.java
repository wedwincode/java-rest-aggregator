package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.api.ParamSpec;

import java.util.List;

// todo: problem we use "out" apiclient inside "in" logic
public interface ApiClient {
    ApiDefinition definition();
    AggregatedRecord getApiResponse(ApiParams params, HttpExecutor executor);

    default ApiId id() { return definition().id(); }
    default String url() { return definition().url(); }
    default String displayName() { return definition().displayName(); }
    default List<ParamSpec> supportedParams() { return definition().supportedParams(); }
}
