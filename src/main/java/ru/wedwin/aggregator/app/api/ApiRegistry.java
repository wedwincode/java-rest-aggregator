package ru.wedwin.aggregator.app.api;

import ru.wedwin.aggregator.domain.api.ApiDefinition;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.app.port.out.ApiClient;

import java.util.List;

public interface ApiRegistry {
    void put(ApiClient client);
    ApiClient get(ApiId id);
    ApiDefinition getDefinition(ApiId id);
    List<ApiDefinition> list();
    boolean contains(ApiId id);
}
