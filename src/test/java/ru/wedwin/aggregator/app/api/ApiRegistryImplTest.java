package ru.wedwin.aggregator.app.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.app.port.out.ApiClient;
import ru.wedwin.aggregator.app.port.out.HttpExecutor;
import ru.wedwin.aggregator.domain.api.ApiDefinition;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.api.ApiParams;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.result.Payload;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiRegistryImplTest {

    private ApiRegistry registry;
    private ApiId id;

    @BeforeEach
    void setUp() {
        registry = new ApiRegistryImpl();
        id = new ApiId("dummy");
    }

    class DummyApiClient implements ApiClient {

        @Override
        public ApiDefinition definition() {
            return new ApiDefinition("dummy", "https://google.com/", "test");
        }

        @Override
        public AggregatedItem getApiResponse(ApiParams params, HttpExecutor httpExecutor) {
            return new AggregatedItem(id, new Payload.PBool(true));
        }
    }


    @Test
    void givenRegisteredClient_whenGet_thenReturnsClient() {
        ApiClient client = new DummyApiClient();
        registry.put(client);

        assertEquals(client, registry.get(id));
    }

    @Test
    void givenUnknownId_whenGet_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> registry.get(new ApiId("unknown")));
    }

    @Test
    void givenRegisteredClient_whenGetDefinition_thenReturnsDefinition() {
        ApiClient client = new DummyApiClient();
        registry.put(client);

        assertEquals(client.definition(), registry.getDefinition(id));
    }

    @Test
    void givenRegisteredClient_whenList_thenReturnsDefinitions() {
        ApiClient client = new DummyApiClient();
        registry.put(client);

        assertEquals(List.of(client.definition()), registry.list());
    }

    @Test
    void givenRegisteredClient_whenContains_thenReturnsTrueElseFalse() {
        ApiClient client = new DummyApiClient();
        registry.put(client);

        assertTrue(registry.contains(id));
        assertFalse(registry.contains(new ApiId("unknown")));
    }
}
