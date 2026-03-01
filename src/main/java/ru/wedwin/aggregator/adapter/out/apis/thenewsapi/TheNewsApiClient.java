package ru.wedwin.aggregator.adapter.out.apis.thenewsapi;

import ru.wedwin.aggregator.adapter.out.apis.AbstractApiClient;
import ru.wedwin.aggregator.adapter.out.apis.thenewsapi.dto.TheNewsApiResponse;
import ru.wedwin.aggregator.adapter.out.common.EnvReader;
import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ParamSpec;

public class TheNewsApiClient extends AbstractApiClient<TheNewsApiResponse> {
    @Override
    public ApiDefinition definition() {
        return new ApiDefinition(
                "thenewsapi",
                "https://api.thenewsapi.com/v1/news/top",
                "The News API Client",
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
