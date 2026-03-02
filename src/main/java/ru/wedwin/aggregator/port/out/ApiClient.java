package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.api.ParamMeta;

import java.net.URL;
import java.util.List;

public interface ApiClient {
    ApiDefinition definition();
    AggregatedItem getApiResponse(ApiParams params, Executor executor);

    default ApiId id() { return definition().id(); }
    default URL url() { return definition().url(); }
    default List<ParamMeta> supportedParams() { return definition().supportedParams(); }
}
