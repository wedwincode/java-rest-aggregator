package ru.wedwin.aggregator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.adapter.in.cli.CliApp;
import ru.wedwin.aggregator.adapter.out.apis.newsapi.NewsApiClient;
import ru.wedwin.aggregator.adapter.out.apis.thenewsapi.TheNewsApiClient;
import ru.wedwin.aggregator.adapter.out.apis.weatherapi.WeatherApiClient;
import ru.wedwin.aggregator.adapter.out.http.OkHttpExecutor;
import ru.wedwin.aggregator.adapter.out.output.CsvWriter;
import ru.wedwin.aggregator.adapter.out.output.JsonWriter;
import ru.wedwin.aggregator.app.AggregationUseCase;
import ru.wedwin.aggregator.app.registry.ApiRegistry;
import ru.wedwin.aggregator.app.registry.WriterRegistry;

import java.util.List;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        ApiRegistry apiRegistry = new ApiRegistry(
                List.of(new NewsApiClient(), new TheNewsApiClient(), new WeatherApiClient()));
        WriterRegistry writerRegistry = new WriterRegistry(List.of(new JsonWriter(), new CsvWriter()));
        AggregationUseCase useCase = new AggregationUseCase(
                new CliApp(args, apiRegistry, writerRegistry),
                new OkHttpExecutor(),
                apiRegistry,
                writerRegistry
        );
        try {
            useCase.run();
        } catch (Exception e) {
            log.error("interactive mode failed", e); // todo think more about logs
        }
    }
}
