package ru.wedwin.aggregator.app;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.app.registry.ApiRegistry;
import ru.wedwin.aggregator.domain.model.in.RunRequest;
import ru.wedwin.aggregator.app.registry.WriterRegistry;
import ru.wedwin.aggregator.port.in.RunRequestRetriever;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.HttpExecutor;
import ru.wedwin.aggregator.port.out.OutputWriter;

import java.util.ArrayList;
import java.util.List;

public class AggregationUseCase {
    private final RunRequestRetriever runRequestRetriever;
    private final HttpExecutor httpExecutor;
    private final ApiRegistry apiRegistry;
    private final WriterRegistry writerRegistry;

    public AggregationUseCase(
            RunRequestRetriever runRequestRetriever,
            HttpExecutor httpExecutor,
            ApiRegistry apiRegistry,
            WriterRegistry writerRegistry
    ) {
        this.runRequestRetriever = runRequestRetriever;
        this.httpExecutor = httpExecutor;
        this.apiRegistry = apiRegistry;
        this.writerRegistry = writerRegistry;
    }

    public void run() throws Exception {
        RunRequest runRequest = runRequestRetriever.getRunRequest();
        List<AggregatedRecord> responseList = new ArrayList<>();

        for (ApiId id: runRequest.apisWithParams().keySet()) {
            ApiClient client = apiRegistry.require(id);
            ApiParams params = runRequest.apisWithParams().getOrDefault(id, ApiParams.of());
            responseList.add(client.getApiResponse(params, httpExecutor));
        }
        OutputWriter writer = writerRegistry.require(runRequest.outputSpec().format().name().toLowerCase()); // todo better
        writer.write(responseList, runRequest.outputSpec());
    }
}
