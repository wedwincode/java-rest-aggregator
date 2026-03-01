package ru.wedwin.aggregator.port.out;

import ru.wedwin.aggregator.domain.model.api.ApiId;

public interface ApiClientProvider {
    ApiClient getClient(ApiId id);
}
