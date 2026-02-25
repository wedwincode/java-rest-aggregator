package ru.wedwin.aggregator;

import ru.wedwin.aggregator.adapter.in.cli.CliApp;
import ru.wedwin.aggregator.adapter.out.apis.newsapi.NewsApiClient;
import ru.wedwin.aggregator.adapter.out.apis.thenewsapi.TheNewsApiClient;
import ru.wedwin.aggregator.adapter.out.http.OkHttpExecutor;
import ru.wedwin.aggregator.adapter.out.output.JsonFileWriter;
import ru.wedwin.aggregator.app.AggregationUseCase;

import java.util.List;

// OkHttp + Jackson + Apache Commons csv
// https://api.thenewsapi.com/v1/news/top?api_token=4kF9JhJJiGfYxz21GnI4pSww7Qw9FYApwHUqxq5A&locale=us&limit=3
// https://api.weatherapi.com/v1/current.json?q=Washington&lang=US&key=77fa9361597645f3a63185222261202
// https://newsapi.org/v2/everything?q=java&apiKey=78b2f2c9610a459ba9b59d7369511db7&pagesize=10
public class Main {
    public static void main(String[] args) {
        AggregationUseCase useCase = new AggregationUseCase(
                new CliApp(args),
                new OkHttpExecutor(),
                List.of(new NewsApiClient(), new TheNewsApiClient()),
                List.of(new JsonFileWriter())
        );

        useCase.run();
    }
}
