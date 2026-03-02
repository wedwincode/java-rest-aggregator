package ru.wedwin.aggregator.app.service;

import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.port.out.ApiClient;

public interface ApiClientProvider {
    ApiClient getClient(ApiId id);
}
