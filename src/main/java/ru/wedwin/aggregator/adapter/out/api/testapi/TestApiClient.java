package ru.wedwin.aggregator.adapter.out.api.testapi;

import ru.wedwin.aggregator.adapter.out.api.AbstractApiClient;
import ru.wedwin.aggregator.adapter.out.api.testapi.dto.TestApiResponse;
import ru.wedwin.aggregator.domain.api.ApiDefinition;

public class TestApiClient extends AbstractApiClient<TestApiResponse> {
    @Override
    public ApiDefinition definition() {
        return new ApiDefinition(
                "testapi",
                "http://localhost:1010/testendpoint",
                "Test API Client"
        );
    }

    @Override
    protected Class<TestApiResponse> dtoClass() {
        return TestApiResponse.class;
    }
}
