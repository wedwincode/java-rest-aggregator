package ru.wedwin.aggregator.app.service.api;

import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.port.out.ApiClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiRegistryImpl implements ApiRegistry {
    private final Map<ApiId, ApiClient> byId;

    public ApiRegistryImpl(List<ApiClient> clients) {
        byId = clients.stream().collect(Collectors.toMap(ApiClient::id, c -> c));
    }

    @Override
    public List<ApiDefinition> list() {
        return byId.values().stream().map(ApiClient::definition).toList();
    }

    @Override
    public boolean contains(ApiId id) {
        return byId.containsKey(id);
    }

    @Override
    public ApiDefinition getDefinition(ApiId id) {
        return getClient(id).definition();
    }

    @Override
    public ApiClient getClient(ApiId id) {
        ApiClient client = byId.get(id);
        if (client == null) {
            throw new IllegalArgumentException("unknown API: " + id);
        }

        return client;
    }
}
