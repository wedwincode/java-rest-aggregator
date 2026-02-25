package ru.wedwin.aggregator.adapter.out.apis.thenewsapi;

import ru.wedwin.aggregator.adapter.out.apis.AbstractApiClient;
import ru.wedwin.aggregator.adapter.out.apis.thenewsapi.dto.TheNewsApiResponse;
import ru.wedwin.aggregator.adapter.out.common.EnvReader;
import ru.wedwin.aggregator.domain.model.ApiId;
import ru.wedwin.aggregator.domain.model.ParamSpec;

import java.util.List;

public class TheNewsApiClient extends AbstractApiClient<TheNewsApiResponse> {
    @Override
    public ApiId id() {
        return new ApiId("thenewsapi");
    }
    //https://api.thenewsapi.com/v1/news/top?api_token=4kF9JhJJiGfYxz21GnI4pSww7Qw9FYApwHUqxq5A&locale=us&limit=3
    @Override
    public String url() {
        return "https://api.thenewsapi.com/v1/news/top";
    }

    @Override
    public String displayName() {
        return "The News API Client";
    }

    @Override
    public List<ParamSpec> supportedParams() {
        return List.of(
                new ParamSpec("api_token", true, EnvReader.get("THE_NEWS_API_KEY"), "api key (put it in the .env file)"),
                new ParamSpec("locale", false, "us", "language of the news"),
                new ParamSpec("limit", false, "3", "amount of news you want to get")
        );
    }

    @Override
    protected Class<TheNewsApiResponse> dtoClass() {
        return TheNewsApiResponse.class;
    }
}
