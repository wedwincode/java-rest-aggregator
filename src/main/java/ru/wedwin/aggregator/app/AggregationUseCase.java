package ru.wedwin.aggregator.app;

import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.ApiId;
import ru.wedwin.aggregator.domain.model.ApiParams;
import ru.wedwin.aggregator.domain.model.ApiRegistry;
import ru.wedwin.aggregator.domain.model.RunRequest;
import ru.wedwin.aggregator.domain.model.WriterRegistry;
import ru.wedwin.aggregator.port.in.RunRequestRetriever;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.HttpExecutor;
import ru.wedwin.aggregator.port.out.OutputWriter;

import java.util.ArrayList;
import java.util.List;

public class AggregationUseCase {
    private final RunRequestRetriever runRequestRetriever;
    private final HttpExecutor httpExecutor;
    private final List<ApiClient> apiClients;
    private final List<OutputWriter> outputWriters;

    public AggregationUseCase(
            RunRequestRetriever runRequestRetriever,
            HttpExecutor httpExecutor,
            List<ApiClient> apiClients,
            List<OutputWriter> outputWriters
    ) {
        this.runRequestRetriever = runRequestRetriever;
        this.httpExecutor = httpExecutor;
        this.apiClients = apiClients;
        this.outputWriters = outputWriters;
    }

    public void run() {
        RunRequest runRequest = runRequestRetriever.getRunRequest();
        List<AggregatedRecord> responseList = new ArrayList<>();
        ApiRegistry apiRegistry = new ApiRegistry(apiClients);
        WriterRegistry writerRegistry = new WriterRegistry(outputWriters);

        for (ApiId id: runRequest.apisWithParams().keySet()) {
            ApiClient client = apiRegistry.require(id);
            ApiParams params = runRequest.apisWithParams().getOrDefault(id, ApiParams.of(null));
            responseList.add(client.getApiResponse(params, httpExecutor));
        }
        OutputWriter writer = writerRegistry.require(runRequest.outputSpec().format().name().toLowerCase()); // todo better
//        OutputWriter writer = writerRegistry.require("console"); // todo better
        writer.write(responseList, runRequest.outputSpec());
    }
}
