package ru.wedwin.aggregator.app.service.api;

import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.port.out.ApiClient;

import java.util.List;

public interface ApiRegistry {
    ApiDefinition getDefinition(ApiId id);
    boolean contains(ApiId id);
    List<ApiDefinition> list();
    ApiClient getClient(ApiId id);
}
