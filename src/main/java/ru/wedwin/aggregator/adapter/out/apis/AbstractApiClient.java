package ru.wedwin.aggregator.adapter.out.apis;

import ru.wedwin.aggregator.adapter.out.common.JacksonObjectMapper;
import ru.wedwin.aggregator.adapter.out.common.RequestBuilder;
import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.domain.model.Payload;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.Executor;

public abstract class AbstractApiClient<DTO> implements ApiClient {
    protected abstract Class<DTO> dtoClass();

    @Override
    public AggregatedRecord getApiResponse(ApiParams params, Executor executor) {
        params.addDefaultParams(supportedParams());
        String response = executor.execute(RequestBuilder.build(url(), params));
        DTO dto = JacksonObjectMapper.map(response, dtoClass());
        Payload payload = JacksonObjectMapper.fromDto(dto);
        return new AggregatedRecord(id(), payload);
    }
}
