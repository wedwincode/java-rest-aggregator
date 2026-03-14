package ru.wedwin.aggregator.adapter.out.api;

import ru.wedwin.aggregator.adapter.out.common.JacksonObjectMapper;
import ru.wedwin.aggregator.adapter.out.executor.ExecutorException;
import ru.wedwin.aggregator.domain.api.exception.ApiResponseException;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.result.Payload;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.Executor;

public abstract class AbstractApiClient<DTO> implements ApiClient {
    protected abstract Class<DTO> dtoClass();

    @Override
    public AggregatedItem getApiResponse(ApiParams params, Executor executor) throws ApiResponseException {
        params.addDefaultParams(supportedParams());

        String response;
        try {
            response = executor.execute(url(), params.asMap());
        } catch (ExecutorException e) {
            throw new ApiResponseException("api response error: " + e.getMessage(), e);
        }

        DTO dto = JacksonObjectMapper.map(response, dtoClass());
        Payload payload = JacksonObjectMapper.fromDto(dto);

        return new AggregatedItem(id(), payload);
    }
}
