package ru.wedwin.aggregator.adapter.out.api;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.adapter.out.http.ExecutorException;
import ru.wedwin.aggregator.app.port.out.HttpExecutor;
import ru.wedwin.aggregator.domain.api.ApiDefinition;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.api.ParamMeta;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.result.Payload;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractApiClientTest {

    @Test
    void givenParamsWithoutRequiredDefaults_whenGetApiResponse_thenAddsDefaultsAndReturnsAggregatedItem() {
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        ApiParams params = ApiParams.of();
        TestApiClient client = new TestApiClient();

        when(httpExecutor.execute(
                eq(client.url()),
                eq(Map.of("key", "value"))
        )).thenReturn("{\"value\":123}");

        AggregatedItem item = client.getApiResponse(params, httpExecutor);
        Payload.PObject expectedPayload = new Payload.PObject(Map.of("value", new Payload.PInt(123)));

        assertEquals(new ApiId("testapi"), item.apiId());
        assertInstanceOf(Payload.PObject.class, item.payload());
        assertEquals(expectedPayload, item.payload());
        assertEquals(Map.of("key", "value"), params.asMap());
    }

    @Test
    void givenExecutorFailure_whenGetApiResponse_thenThrowsApiResponseException() {
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        ApiParams params = ApiParams.of();
        TestApiClient client = new TestApiClient();

        when(httpExecutor.execute(eq(client.url()), anyMap()))
                .thenThrow(new ExecutorException("boom"));

        ApiResponseException exception = assertThrows(
                ApiResponseException.class,
                () -> client.getApiResponse(params, httpExecutor)
        );

        assertEquals("api response error: boom", exception.getMessage());
    }

    private static final class TestApiClient extends AbstractApiClient<TestDto> {

        @Override
        protected Class<TestDto> dtoClass() {
            return TestDto.class;
        }

        @Override
        public ApiDefinition definition() {
            return new ApiDefinition(
                    "testapi",
                    "https://example.com/",
                    "Test API",
                    new ParamMeta("key", true, "value", "desc")
            );
        }
    }

    record TestDto(int value) {
    }
}
