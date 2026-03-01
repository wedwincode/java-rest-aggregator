package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.api.ParamMeta;

import java.util.List;

// todo: problem we use "out" apiclient inside "in" logic
public interface ApiClient {
    ApiDefinition definition();
    AggregatedItem getApiResponse(ApiParams params, Executor executor);

    default ApiId id() { return definition().id(); }
    default String url() { return definition().url(); }
    default String displayName() { return definition().displayName(); }
    default List<ParamMeta> supportedParams() { return definition().supportedParams(); }
}
