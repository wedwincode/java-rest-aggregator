package ru.wedwin.aggregator.app;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.app.registry.ApiRegistry;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.app.registry.FormatterRegistry;
import ru.wedwin.aggregator.port.in.RunConfigProvider;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.Executor;
import ru.wedwin.aggregator.port.out.Formatter;
import ru.wedwin.aggregator.port.out.ResultStorage;

import java.util.ArrayList;
import java.util.List;

public class AggregationUseCase {
    private final RunConfigProvider runConfigProvider;
    private final Executor executor;
    private final ApiRegistry apiRegistry;
    private final ResultStorage storage;

    public AggregationUseCase(
            RunConfigProvider runConfigProvider,
            Executor executor,
            ApiRegistry apiRegistry,
            ResultStorage storage
    ) {
        this.runConfigProvider = runConfigProvider;
        this.executor = executor;
        this.apiRegistry = apiRegistry;
        this.storage = storage;
    }

    public void run() {
        RunConfig runConfig = runConfigProvider.getRunConfig();
        List<AggregatedItem> responseList = new ArrayList<>();

        for (ApiId id: runConfig.apisWithParams().keySet()) {
            ApiClient client = apiRegistry.getClient(id);
            ApiParams params = runConfig.apisWithParams().getOrDefault(id, ApiParams.of());
            responseList.add(client.getApiResponse(params, executor));
        }
        storage.save(runConfig.outputSpec(), responseList);

        switch (runConfig.displaySpec().mode()) {
            case NONE -> {}
            case ALL -> storage.printAll(runConfig.outputSpec(), System.out); // todo
            case BY_API -> storage.printByApi(runConfig.outputSpec(), runConfig.displaySpec().apiId(), System.out);
        }
    }
}
