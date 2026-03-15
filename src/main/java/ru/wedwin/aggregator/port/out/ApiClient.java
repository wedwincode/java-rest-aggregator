package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.api.ApiDefinition;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.api.ParamMeta;

import java.net.URL;
import java.util.List;

public interface ApiClient {
    ApiDefinition definition();
    AggregatedItem getApiResponse(ApiParams params, HttpExecutor httpExecutor);

    default ApiId id() { return definition().id(); }
    default URL url() { return definition().url(); }
    default List<ParamMeta> supportedParams() { return definition().supportedParams(); }
}
