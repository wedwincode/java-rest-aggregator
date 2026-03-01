package ru.wedwin.aggregator.adapter.out.apis.newsapi;

import ru.wedwin.aggregator.adapter.out.apis.AbstractApiClient;
import ru.wedwin.aggregator.adapter.out.apis.newsapi.dto.NewsApiResponse;
import ru.wedwin.aggregator.adapter.out.common.EnvReader;
import ru.wedwin.aggregator.domain.model.api.ApiDefinition;
import ru.wedwin.aggregator.domain.model.api.ParamMeta;

public class NewsApiClient extends AbstractApiClient<NewsApiResponse> {
    @Override
    public ApiDefinition definition() {
        return new ApiDefinition(
                "newsapi",
                "https://newsapi.org/v2/everything",
                "News API Client",
                new ParamMeta("apiKey", true, EnvReader.get("NEWS_API_KEY"), "api key (put it in the .env file)"),
                new ParamMeta("q", true, "java", "your query"),
                new ParamMeta("pagesize", false, "3", "amount of news you want to get")
        );
    }

    @Override
    protected Class<NewsApiResponse> dtoClass() {
        return NewsApiResponse.class;
    }
}
