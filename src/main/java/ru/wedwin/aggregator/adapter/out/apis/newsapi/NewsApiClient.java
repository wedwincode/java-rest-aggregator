package ru.wedwin.aggregator.adapter.out.apis.newsapi;

import ru.wedwin.aggregator.adapter.out.apis.newsapi.dto.NewsApiResponse;
import ru.wedwin.aggregator.adapter.out.common.EnvReader;
import ru.wedwin.aggregator.adapter.out.common.JacksonObjectMapper;
import ru.wedwin.aggregator.adapter.out.common.RequestBuilder;
import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.ApiId;
import ru.wedwin.aggregator.domain.model.ApiParams;
import ru.wedwin.aggregator.domain.model.ParamSpec;
import ru.wedwin.aggregator.port.out.ApiClient;
import ru.wedwin.aggregator.port.out.HttpExecutor;

import java.util.List;

public class NewsApiClient implements ApiClient {

    @Override
    public ApiId id() {
        return new ApiId("newsapiclient");
    }

    @Override
    public String url() {
        return "https://newsapi.org/v2/everything";
    }

    @Override
    public String displayName() {
        return "News API Client";
    }

    @Override
    public List<ParamSpec> supportedParams() {
        return List.of(
                new ParamSpec("q", true, "java", "your query"),
                new ParamSpec("apiKey", true, EnvReader.get("NEWS_API_KEY"), "api key (put it in the .env file)"),
                new ParamSpec("pagesize", false, "10", "amount of news you want to get")
        );
    }

    @Override
    public List<AggregatedRecord> getApiResponse(ApiParams params, HttpExecutor executor) {
        params.addDefaultParams(supportedParams());
        String response = executor.execute(RequestBuilder.build(url(), params));
        NewsApiResponse mappedResponse = JacksonObjectMapper.map(response, NewsApiResponse.class);
        return List.of();
    }
}
