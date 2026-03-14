package ru.wedwin.aggregator.app.service.api;

import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.port.out.ApiClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum ApiRegistryImpl implements ApiRegistry {
    INSTANCE;

    private final Map<ApiId, ApiClient> byId;

    ApiRegistryImpl() {
        byId = new LinkedHashMap<>();
    }

    @Override
    public void put(ApiClient client) {
        byId.put(client.id(), client);
    }

    @Override
    public ApiClient get(ApiId id) {
        ApiClient client = byId.get(id);
        if (client == null) {
            throw new IllegalArgumentException("unknown API: " + id);
        }

        return client;
    }

    @Override
    public ApiDefinition getDefinition(ApiId id) {
        return get(id).definition();
    }

    @Override
    public List<ApiDefinition> list() {
        return byId.values().stream().map(ApiClient::definition).toList();
    }

    @Override
    public boolean contains(ApiId id) {
        return byId.containsKey(id);
    }
}
