package ru.wedwin.aggregator.adapter.out.api;

import ru.wedwin.aggregator.adapter.out.common.JacksonObjectMapper;
import ru.wedwin.aggregator.adapter.out.common.RequestBuilder;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.result.Payload;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.Executor;

public abstract class AbstractApiClient<DTO> implements ApiClient {
    protected abstract Class<DTO> dtoClass();

    @Override
    public AggregatedItem getApiResponse(ApiParams params, Executor executor) {
        params.addDefaultParams(supportedParams());
        String response = executor.execute(RequestBuilder.buildGet(url(), params));
        DTO dto = JacksonObjectMapper.map(response, dtoClass());
        Payload payload = JacksonObjectMapper.fromDto(dto);
        return new AggregatedItem(id(), payload);
    }
}
