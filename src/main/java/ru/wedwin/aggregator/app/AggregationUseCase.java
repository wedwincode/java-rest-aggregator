package ru.wedwin.aggregator.app;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.api.ApiParams;
import ru.wedwin.aggregator.app.registry.ApiRegistry;
import ru.wedwin.aggregator.domain.model.in.RunRequest;
import ru.wedwin.aggregator.app.registry.WriterRegistry;
import ru.wedwin.aggregator.port.in.RunRequestRetriever;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.Executor;
import ru.wedwin.aggregator.port.out.Writer;

import java.util.ArrayList;
import java.util.List;

public class AggregationUseCase {
    private final RunRequestRetriever runRequestRetriever;
    private final Executor executor;
    private final ApiRegistry apiRegistry;
    private final WriterRegistry writerRegistry;

    public AggregationUseCase(
            RunRequestRetriever runRequestRetriever,
            Executor executor,
            ApiRegistry apiRegistry,
            WriterRegistry writerRegistry
    ) {
        this.runRequestRetriever = runRequestRetriever;
        this.executor = executor;
        this.apiRegistry = apiRegistry;
        this.writerRegistry = writerRegistry;
    }

    public void run() throws Exception {
        RunRequest runRequest = runRequestRetriever.getRunRequest();
        List<AggregatedRecord> responseList = new ArrayList<>();

        for (ApiId id: runRequest.apisWithParams().keySet()) {
            ApiClient client = apiRegistry.getClient(id);
            ApiParams params = runRequest.apisWithParams().getOrDefault(id, ApiParams.of());
            responseList.add(client.getApiResponse(params, executor));
        }
        Writer writer = writerRegistry.getWriter(runRequest.outputSpec().writerId()); // todo better
        writer.write(responseList, runRequest.outputSpec());
    }
}
