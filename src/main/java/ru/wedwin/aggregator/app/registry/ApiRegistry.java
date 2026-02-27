package ru.wedwin.aggregator.app.registry;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.port.out.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiRegistry {
    private final Map<ApiId, ApiClient> byId;

    public ApiRegistry(List<ApiClient> clients) {
        byId = clients.stream().collect(Collectors.toMap(ApiClient::id, c -> c));
    }

    public ApiClient require(ApiId id) {
        ApiClient client = byId.get(id);
        if (client == null) {
            throw new IllegalArgumentException("unknown API: " + id);
        }
        return client;
    }

    public List<ApiClient> all() {
        return new ArrayList<>(byId.values());
    }
}
