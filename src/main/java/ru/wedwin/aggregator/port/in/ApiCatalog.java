package ru.wedwin.aggregator.port.in;

import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ApiId;

import java.util.List;

public interface ApiCatalog {
    ApiDefinition getDefinition(ApiId id);
    List<ApiDefinition> list();
}
