package ru.wedwin.aggregator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.adapter.in.cli.CliRunConfigProvider;
import ru.wedwin.aggregator.adapter.out.api.newsapi.NewsApiClient;
import ru.wedwin.aggregator.adapter.out.api.thenewsapi.TheNewsApiClient;
import ru.wedwin.aggregator.adapter.out.api.weatherapi.WeatherApiClient;
import ru.wedwin.aggregator.adapter.out.codec.CsvCodec;
import ru.wedwin.aggregator.adapter.out.codec.JsonCodec;
import ru.wedwin.aggregator.adapter.out.executor.OkHttpExecutor;
import ru.wedwin.aggregator.adapter.out.saver.FileResultSaver;
import ru.wedwin.aggregator.adapter.out.viewer.ConsoleResultViewer;
import ru.wedwin.aggregator.app.AggregationUseCase;
import ru.wedwin.aggregator.app.service.ApiRegistry;
import ru.wedwin.aggregator.app.service.CodecRegistry;

import java.util.List;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        ApiRegistry apiRegistry = new ApiRegistry(List.of(
                new NewsApiClient(),
                new TheNewsApiClient(),
                new WeatherApiClient()
        ));
        CodecRegistry codecRegistry = new CodecRegistry(List.of(
                new JsonCodec(),
                new CsvCodec()
        ));

        AggregationUseCase useCase = new AggregationUseCase(
                new CliRunConfigProvider(args, apiRegistry, codecRegistry, System.in, System.out),
                new OkHttpExecutor(),
                apiRegistry,
                new FileResultSaver(codecRegistry),
                new ConsoleResultViewer(codecRegistry)
        );

        try {
            useCase.run();
        } catch (Exception e) {
            log.error("error", e); // todo think more about logs
            System.exit(2);
        }
    }
}
