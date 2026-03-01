package ru.wedwin.aggregator.app;

import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.app.registry.ApiRegistry;
import ru.wedwin.aggregator.domain.model.config.RunConfig;
import ru.wedwin.aggregator.app.registry.WriterRegistry;
import ru.wedwin.aggregator.port.in.RunConfigProvider;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.Executor;
import ru.wedwin.aggregator.port.out.Writer;

import java.util.ArrayList;
import java.util.List;

public class AggregationUseCase {
    private final RunConfigProvider runConfigProvider;
    private final Executor executor;
    private final ApiRegistry apiRegistry;
    private final WriterRegistry writerRegistry;

    public AggregationUseCase(
            RunConfigProvider runConfigProvider,
            Executor executor,
            ApiRegistry apiRegistry,
            WriterRegistry writerRegistry
    ) {
        this.runConfigProvider = runConfigProvider;
        this.executor = executor;
        this.apiRegistry = apiRegistry;
        this.writerRegistry = writerRegistry;
    }

    public void run() throws Exception {
        RunConfig runConfig = runConfigProvider.getRunConfig();
        List<AggregatedItem> responseList = new ArrayList<>();

        for (ApiId id: runConfig.apisWithParams().keySet()) {
            ApiClient client = apiRegistry.getClient(id);
            ApiParams params = runConfig.apisWithParams().getOrDefault(id, ApiParams.of());
            responseList.add(client.getApiResponse(params, executor));
        }
        Writer writer = writerRegistry.getWriter(runConfig.outputSpec().writerId()); // todo better
        writer.write(responseList, runConfig.outputSpec());
    }
}
