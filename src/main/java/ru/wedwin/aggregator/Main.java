package ru.wedwin.aggregator;

import ru.wedwin.aggregator.adapter.in.cli.CliApp;
import ru.wedwin.aggregator.adapter.out.apis.newsapi.NewsApiClient;
import ru.wedwin.aggregator.adapter.out.apis.thenewsapi.TheNewsApiClient;
import ru.wedwin.aggregator.adapter.out.apis.weatherapi.WeatherApiClient;
import ru.wedwin.aggregator.adapter.out.http.OkHttpExecutor;
import ru.wedwin.aggregator.adapter.out.output.CsvWriter;
import ru.wedwin.aggregator.adapter.out.output.JsonWriter;
import ru.wedwin.aggregator.app.AggregationUseCase;

import java.util.List;

// OkHttp + Jackson + Apache Commons csv
public class Main {
    public static void main(String[] args) {
        AggregationUseCase useCase = new AggregationUseCase(
                new CliApp(args),
                new OkHttpExecutor(),
                List.of(new NewsApiClient(), new TheNewsApiClient(), new WeatherApiClient()),
                List.of(new JsonWriter(), new CsvWriter())
        );

        useCase.run();
    }
}
